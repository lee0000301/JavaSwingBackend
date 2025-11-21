package cse.hotel.server;

import cse.hotel.server.service.*;
import cse.hotel.common.model.*;
import cse.hotel.common.exception.DuplicateIdException;
import cse.hotel.common.exception.DataNotFoundException;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import java.io.*;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;

    // 싱글톤 Service 인스턴스를 미리 가져옵니다.
    private final RoomService roomService = RoomService.getInstance();
    private final FoodService foodService = FoodService.getInstance();
    private final CustomerService customerService = CustomerService.getInstance();
    private final ClientReservationService clientReservationService = ClientReservationService.getInstance();
    private final ReservationService reservationService = ReservationService.getInstance();

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream()); 
             ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream())) {

            // 클라이언트로부터 요청 수신
            Request request = (Request) ois.readObject();
            System.out.println("-> [요청 수신] 명령: " + request.getCommand());

            // 요청 처리 후 응답 생성
            Response response = handleRequest(request);

            // 클라이언트에게 응답 전송
            oos.writeObject(response);
            System.out.println("<- [응답 전송] 상태: " + (response.isSuccess() ? "성공" : "실패"));

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("❌ 클라이언트 처리 중 통신 오류 또는 객체 오류: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                /* 무시 */ }
        }
    }

    /**
     * 요청 명령에 따라 적절한 Service 메서드를 호출하고 Response를 생성합니다.
     */
    private Response handleRequest(Request request) {
        String command = request.getCommand();
        Object data = request.getData();

        try {
            switch (command) {
                
                // -----------------예약부분-----------------------
                // 관리자용_ 전체 예약 불러오기 
                case "LOAD_RESERVATIONS":
                    List<?> allReservations = reservationService.loadReservations();
                    return new Response(allReservations, "전체 예약 목록 조회 성공");
                    
                case "RESERVATION_CREATE":
                    // Object data를 Reservation 타입으로 형 변환
                    Reservation reservation = (Reservation) data;

                    boolean result = reservationService.createReservation(reservation);

                    if (result) {
                        return new Response(true, "예약 등록 성공");
                    } else {
                        return new Response(false, "이미 존재하는 예약 번호입니다.");
                    }
                    
                // --- Food (식음료) 관리 ---
                case "GET_FOODS":
                    List<Food> foods = foodService.getAllFoods();
                    return new Response(foods, "전체 식음료 목록 조회 성공");

                case "ADD_FOOD":
                    // Object data를 Food 타입으로 형 변환하여 메서드에 전달
                    foodService.addFood((Food) data);
                    return new Response(null, "식음료 등록 성공");

                case "UPDATE_FOOD":
                    // UPDATE_FOOD 명령 추가 (FoodUI에서 사용됨)
                    foodService.updateFood((Food) data);
                    return new Response(null, "식음료 수정 성공");

                case "DELETE_FOOD":
                    // Object data를 String 타입 (ID)으로 형 변환하여 메서드에 전달
                    foodService.deleteFood((String) data);
                    return new Response(null, "식음료 삭제 성공");

                // --- Customer (고객) 관리 ---
                case "GET_CUSTOMERS":
                    List<Customer> customers = customerService.getAllCustomers();
                    return new Response(customers, "전체 고객 목록 조회 성공");

                case "ADD_CUSTOMER":
                    Customer savedCustomer = customerService.addCustomer((Customer) data);
                    return new Response(savedCustomer, "고객 등록 성공");

                case "UPDATE_CUSTOMER":
                    customerService.updateCustomer((Customer) data);
                    return new Response(null, "고객 정보 수정 성공");

                case "CHECK_IN":
                    // 클라이언트가 보낸 데이터(방 번호)를 받아서 서비스에 전달
                    roomService.checkIn((Integer) data);
                    return new Response(null, "체크인 처리가 완료되었습니다.");

                case "CHECK_OUT":
                    roomService.checkOut((Integer) data);
                    return new Response(null, "체크아웃 처리가 완료되었습니다.");

                case "FINISH_CLEANING":
                    roomService.finishCleaning((Integer) data);
                    return new Response(null, "청소가 완료되었습니다. 객실이 배정 가능 상태로 변경됩니다.");

                case "DELETE_CUSTOMER":
                    customerService.deleteCustomer((String) data);
                    return new Response(null, "고객 삭제 성공");

                // --- Room (객실) 관리 ---
                case "GET_ROOMS":
                    List<Room> rooms = roomService.getAllRooms();
                    return new Response(rooms, "전체 객실 목록 조회 성공");

                case "ADD_ROOM":
                    roomService.addRoom((Room) data);
                    return new Response(null, "객실 등록 성공");

                case "UPDATE_ROOM":
                    roomService.updateRoom((Room) data);
                    return new Response(null, "객실 수정 성공");

                case "DELETE_ROOM":
                    // Room은 ID가 int이므로 int로 형 변환 필요
                    roomService.deleteRoom((Integer) data);
                    return new Response(null, "객실 삭제 성공");

                case "MAKE_RESERVATION":
                    ClientReservation reqRes = (ClientReservation) data;
                    System.out.println("-> 예약 요청 수신: " + reqRes.getCustomerId() + ", 방: " + reqRes.getRoomNumber());

                    try {
                        // 1. 예약 정보 저장 (이게 없으면 '내 예약'에 안 뜸)
                        ClientReservation savedRes = clientReservationService.makeReservation(
                                reqRes.getCustomerId(),
                                reqRes.getRoomNumber(),
                                reqRes.getCheckInDate(),
                                reqRes.getCheckOutDate(),
                                reqRes.getTotalPrice()
                        );

                        // 2. [중요] 방 상태 변경 (이게 없으면 방이 계속 '빈 방'으로 나옴)
                        roomService.reserveRoom(reqRes.getRoomNumber());

                        return new Response(savedRes, "예약 성공!");

                    } catch (Exception e) {
                        e.printStackTrace(); // 서버 콘솔에 에러 찍기
                        return new Response("예약 실패: " + e.getMessage());
                    }

                // 사용자 기준_ 예약 목록 조회
                case "GET_MY_RESERVATIONS":
                    String custId = (String) data; // 고객 ID가 넘어옴
                    List<ClientReservation> myReservations = clientReservationService.getReservationsByCustomerId(custId);
                    return new Response(myReservations, "조회 성공");

                // 사용자 기준_  예약 취소 (중요: 예약 취소 + 방 복구)
                case "CANCEL_RESERVATION":
                    String resId = (String) data; // 예약 ID가 넘어옴
                    try {
                        // 1) 예약 상태 취소하고 방 번호 받아오기
                        int roomNum = clientReservationService.cancelReservation(resId);

                        // 2) 해당 방을 다시 '빈 방'으로 만들기
                        roomService.cancelBooking(roomNum);

                        return new Response(null, "예약이 정상적으로 취소되었습니다.");
                    } catch (Exception e) {
                        return new Response("취소 실패: " + e.getMessage());
                    }

                // --- 기본 ---
                default:
                    return new Response("알 수 없는 요청 명령입니다.");
            }
        } // --- Service 예외 처리 (실패 응답 생성) ---
        catch (DuplicateIdException | DataNotFoundException | IllegalArgumentException e) {
            // 비즈니스 로직 예외는 실패 메시지로 클라이언트에게 전달
            return new Response(e.getMessage());
        } catch (Exception e) {
            // 예상치 못한 서버 내부 오류 (NullPointer, ClassCast 등)
            e.printStackTrace();
            return new Response("서버 내부 처리 중 알 수 없는 오류 발생: " + e.getMessage());
        }
    }
}
