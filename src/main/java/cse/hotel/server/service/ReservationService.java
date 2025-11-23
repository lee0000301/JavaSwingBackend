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
    
public void updateReservationStatus(int roomNumber, String newStatus) {
        System.out.println("\n=== [상태 변경 시작] " + roomNumber + "호 -> " + newStatus + " ===");
        
        // 1. 파일 로드
        List<Reservation> list = repository.loadReservations();
        System.out.println("1. 파일 로드 완료 (총 예약 수: " + list.size() + "개)");
        
        boolean isUpdated = false;

        // 2. 검색 및 수정
        for (Reservation r : list) {
            // 디버깅: 현재 검사 중인 데이터 출력
            // System.out.println("   - 검사중: 방번호=" + r.getRoomNo() + ", 상태=" + r.getStatus());

            // 방 번호가 일치하는지 확인
            if (r.getRoomNo() == roomNumber) {
                
                // [중요] 이미 끝난 예약이나 다른 상태인 경우를 구별해야 한다면 조건 추가
                // 여기서는 일단 해당 방 번호의 '모든 유효한 예약'을 바꿉니다.
                // (빈 객실이 아니거나, 혹은 체크인하려는 경우)
                
                System.out.println("   >>> [타겟 발견!] 기존 상태: " + r.getStatus());
                
                r.setStatus(newStatus); // 상태 변경
                isUpdated = true;
                
                // 유효한 예약 하나만 바꾸고 싶다면 break; 를 넣으세요.
                // break; 
            }
        }

        // 3. 저장
        if (isUpdated) {
            repository.saveReservations(list); // ★ 파일 저장 ★
            System.out.println("2. [저장 성공] 파일에 덮어쓰기 완료.");
        } else {
            System.out.println("2. [저장 실패] " + roomNumber + "호에 해당하는 예약 정보를 리스트에서 못 찾았습니다.");
        }
        System.out.println("==============================\n");
    }
    
    public int cancelReservation(String targetNo) throws DataNotFoundException {
        List<Reservation> reservationList = repository.loadReservations();
        Reservation target = null;
        int roomNumber = -1;

        System.out.println("\n========== [디버깅 시작] ==========");
        System.out.println("1. 클라이언트가 보낸 삭제 요청 번호: [" + targetNo + "]");
        System.out.println("2. 현재 서버가 가진 예약 목록 (" + reservationList.size() + "개):");

        for (Reservation r : reservationList) {
            // 서버에 저장된 값들을 따옴표([])로 감싸서 공백 여부 확인
            String serverNo = r.getReservationNo();
            String serverId = r.getReservationId(); // 혹시 모르니 이것도 확인
            
            System.out.println("   - 검사 중인 객체: No=[" + serverNo + "], ID=[" + serverId + "]");

            // 비교 로직 (No 기준)
            if (serverNo != null && serverNo.trim().equals(targetNo.trim())) {
                System.out.println("   >>> [찾았다!] 일치하는 예약 발견!");
                target = r;
                roomNumber = r.getRoomNumber();
                break;
            }
        }
        System.out.println("==================================\n");

        if (target == null) {
            throw new DataNotFoundException("취소 실패: 예약 번호 [" + targetNo + "]와 일치하는 데이터가 서버 리스트에 없습니다.");
        }

        reservationList.remove(target);
        repository.saveReservations(reservationList);
        
        System.out.println("삭제 성공 및 파일 저장 완료.");
        return roomNumber;
    }
}