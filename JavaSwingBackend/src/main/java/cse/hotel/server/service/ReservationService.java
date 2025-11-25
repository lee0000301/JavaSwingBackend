package cse.hotel.server.service;

import cse.hotel.common.exception.DataNotFoundException;
import cse.hotel.common.model.Reservation;
import cse.hotel.server.repository.ReservationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ReservationService {

    // 1. 싱글톤 인스턴스
    private static final ReservationService instance = new ReservationService();

    // ReservationRepository 인스턴스를 가져옵니다.
    private ReservationRepository repository = ReservationRepository.getInstance();

    // 2. private 생성자로 외부 생성 차단
    private ReservationService() {
    }

    // 3. 인스턴스 접근 메서드
    public static ReservationService getInstance() {
        return instance;
    }

    // --- Read (조회) ---

    /**
     * 전체 예약 목록을 반환합니다. (LOAD_RESERVATIONS 명령 처리용)
     */
    public List<Reservation> loadReservations() {
        return repository.loadReservations();
    }

    /**
     * 특정 고객 ID의 예약 목록을 반환합니다. (GET_MY_RESERVATIONS 명령 처리용)
     */
    public List<Reservation> getReservationsByCustomerId(String customerId) {
        return repository.loadReservations().stream()
                .filter(res -> res.getCustomerId().equals(customerId))
                .collect(Collectors.toList());
    }

    // --- Delete (취소) ---

    /**
     * 예약을 취소(삭제)하고, 취소된 객실 번호를 반환합니다. (CANCEL_RESERVATION 명령 처리용)
     * @return 취소된 예약의 객실 번호 (방 상태 복구를 위해 필요)
     * @throws DataNotFoundException 해당 예약 ID가 존재하지 않을 경우 발생
     */
    
    // 신규 예약 만들기
    public Reservation createReservation(Reservation reservation) throws Exception { 
        // 기존 예약 목록 불러오기
        List<Reservation> reservationList = repository.loadReservations();

        // 1. 예약 ID 자동 생성 및 설정
        String newId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        reservation.setReservationId(newId); // <--- ID를 생성하여 객체에 설정

        // 3. 예약 등록
        reservationList.add(reservation);

        // 4. 파일에 저장
        repository.saveReservations(reservationList);

        System.out.println("예약 등록 완료: " + reservation.getReservationId());
        return reservation; 
    }
    
    
    public int cancelReservation(String targetId) throws DataNotFoundException {
        List<Reservation> reservationList = repository.loadReservations();
        Reservation target = null;
        int roomNumber = -1;
        
        System.out.println("--- 예약 취소 요청 (ReservationId 기준): [" + targetId + "] ---");

        // 삭제할 예약 찾기
        for (Reservation r : reservationList) {
            if (r.getReservationId() != null && 
            r.getReservationId().trim().equals(targetId.trim())) {
                target = r;
                roomNumber = r.getRoomNumber(); // 방 번호 백업
                break;
            }
        }

        if (target == null) {
            System.out.println("실패: 목록에서 ReservationId [" + targetId + "]를 찾을 수 없음.");
            throw new DataNotFoundException("취소 실패: 해당 예약 ID(" + targetId + ")를 찾을 수 없습니다.");        }

        // 리스트에서 삭제 및 저장
        reservationList.remove(target);
        repository.saveReservations(reservationList);
        
        System.out.println("예약 취소 완료: " + targetId);
        return roomNumber; // 취소된 방 번호 리턴 (ClientHandler에서 사용)
    }
}