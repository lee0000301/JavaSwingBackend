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
    public boolean createReservation(Reservation reservation) {
    // 기존 예약 목록 불러오기
    List<Reservation> reservationList = repository.loadReservations();
    // 예약번호 중복 검사
    for (Reservation r : reservationList) {
        if (r.getReservationId().equals(reservation.getReservationId())) {
            return false; // 중복됨 → 실패
        }
    }
    // 예약 등록
    reservationList.add(reservation);
    // 파일에 저장
    repository.saveReservations(reservationList);

    System.out.println("예약 등록 완료: " + reservation.getReservationId());
    return true;
    
    }
    
    
    public int cancelReservation(String reservationId) throws DataNotFoundException {
        List<Reservation> reservationList = repository.loadReservations();
        Reservation target = null;
        int roomNumber = -1;

        // 삭제할 예약 찾기
        for (Reservation r : reservationList) {
            if (r.getReservationId().equals(reservationId)) {
                target = r;
                roomNumber = r.getRoomNumber(); // 방 번호 백업
                break;
            }
        }

        if (target == null) {
            throw new DataNotFoundException("오류: 취소하려는 예약 ID를 찾을 수 없습니다. (" + reservationId + ")");
        }

        // 리스트에서 삭제 및 저장
        reservationList.remove(target);
        repository.saveReservations(reservationList);
        
        System.out.println("예약 취소 완료: " + reservationId);
        return roomNumber; // 취소된 방 번호 리턴 (ClientHandler에서 사용)
    }
}