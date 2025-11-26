package cse.hotel.server.service;

import cse.hotel.common.model.ClientReservation;
import cse.hotel.common.model.Payment;
import cse.hotel.server.repository.PaymentRepository;
import cse.hotel.server.repository.FoodOrderRepository;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

public class PaymentService {
    private static final PaymentService instance = new PaymentService();
    private final PaymentRepository paymentRepository = PaymentRepository.getInstance();
    private final FoodOrderRepository foodOrderRepository = FoodOrderRepository.getInstance();
    
    // 예약 정보 조회를 위해 필요
    private final ClientReservationService reservationService = ClientReservationService.getInstance();

    private PaymentService() {}
    public static PaymentService getInstance() { return instance; }

    /**
     * 체크아웃 전, 청구서(Bill) 정보를 계산해서 반환
     */
    public Map<String, Object> calculateBill(String customerId, int roomNumber) throws Exception {
        // 1. 해당 고객의 'CONFIRMED' 상태인 예약 찾기
        List<ClientReservation> reservations = reservationService.getReservationsByCustomerId(customerId);
        ClientReservation targetRes = null;
        
        // ▼▼▼ [추가] 숙박 일수 계산 ▼▼▼
        long days = 0;
        try {
            java.time.LocalDate in = java.time.LocalDate.parse(targetRes.getCheckInDate());
            java.time.LocalDate out = java.time.LocalDate.parse(targetRes.getCheckOutDate());
            days = java.time.temporal.ChronoUnit.DAYS.between(in, out);
        } catch (Exception e) { days = 1; } // 에러 시 기본 1박
        
        
        
        for (ClientReservation r : reservations) {
            if (r.getRoomNumber() == roomNumber && 
               ("CHECKED_IN".equals(r.getStatus()) || "점유중".equals(r.getStatus()))) {
                targetRes = r;
                break;
            }
        }
        
        if (targetRes == null) {
            throw new Exception("해당 객실의 유효한 예약 정보를 찾을 수 없습니다.");
        }

        // 2. 룸서비스 요금 합산 (해당 고객, 해당 방 번호로 주문한 내역)
        double foodTotal = 0;
        StringBuilder foodSb = new StringBuilder();
        
        List<Map<String, Object>> orders = foodOrderRepository.findAll();
        for (Map<String, Object> order : orders) {
            String cId = (String) order.get("customerId");
            int rNum = (int) order.get("roomNumber");
            int price = (int) order.get("totalPrice"); // FoodOrder 저장 시 totalPrice 넣어야 함
            
            if (customerId.equals(cId) && roomNumber == rNum) {
                foodTotal += price;
                
                // ▼▼▼ [추가] 메뉴 이름과 개수 기록 (예: "치킨 x 1, ") ▼▼▼
                String fname = (String) order.get("foodName");
                int cnt = (int) order.get("count");
                foodSb.append(fname).append(" x ").append(cnt).append(", ");
            }
        }
        
        // 끝에 남은 쉼표 제거
        String foodDetails = foodSb.length() > 0 ? 
                             foodSb.substring(0, foodSb.length() - 2) : "없음";

        // 3. 결과 Map 생성
        Map<String, Object> bill = new HashMap<>();
        bill.put("roomFee", targetRes.getTotalPrice());
        bill.put("foodFee", foodTotal);
        bill.put("totalAmount", targetRes.getTotalPrice() + foodTotal);
        bill.put("checkIn", targetRes.getCheckInDate());
        bill.put("checkOut", targetRes.getCheckOutDate());
        bill.put("reservationId", targetRes.getReservationId());
        bill.put("stayDays", (int) days); 
        bill.put("foodItems", foodDetails);

        return bill;
    }

    /**
     * 결제 승인 및 저장
     */
    public void processPayment(Payment payment) {
        paymentRepository.addPayment(payment);
        
        // 결제가 완료되었으므로 해당 예약의 상태를 'COMPLETED' 등으로 변경하는 로직도 추가 가능
        // reservationService.completeReservation(payment.getReservationId()); (선택 사항)
        
        System.out.println("✅ 결제 처리 완료: " + payment.getPaymentId());
    }
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}