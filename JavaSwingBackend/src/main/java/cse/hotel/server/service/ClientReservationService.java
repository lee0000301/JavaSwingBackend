package cse.hotel.server.service;

import cse.hotel.common.exception.DataNotFoundException;
import cse.hotel.server.repository.ClientReservationRepository;
import cse.hotel.common.model.ClientReservation;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientReservationService {

    private static final ClientReservationService instance = new ClientReservationService();
    private final ClientReservationRepository repository = ClientReservationRepository.getInstance();

    private ClientReservationService() {}

    public static ClientReservationService getInstance() {
        return instance;
    }

    // 신규 예약 생성
    public ClientReservation makeReservation(String customerId, int roomNumber, String checkIn, String checkOut, double price) {
        // ID 자동 생성 (RES-XXXXXXX)
        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ClientReservation newRes = new ClientReservation(
                reservationId,
                customerId,
                roomNumber,
                checkIn,
                checkOut,
                price,
                "CONFIRMED"
        );

        repository.add(newRes);
        System.out.println("✅ 예약 생성 완료: " + reservationId);
        return newRes;
    }

    // 고객 ID로 예약 목록 조회
    public List<ClientReservation> getReservationsByCustomerId(String customerId) {
        List<ClientReservation> all = repository.findAll();
        List<ClientReservation> result = new ArrayList<>();
        
        for (ClientReservation r : all) {
            if (r.getCustomerId().equals(customerId)) {
                result.add(r);
            }
        }
        return result;
    }

    // 예약 취소 (상태 변경 후 방 번호 반환)
    public int cancelReservation(String reservationId) throws DataNotFoundException, IllegalStateException {
        ClientReservation res = repository.findById(reservationId);
        
        if (res == null) throw new DataNotFoundException("예약을 찾을 수 없습니다.");
        if ("CANCELLED".equals(res.getStatus())) throw new IllegalStateException("이미 취소된 예약입니다.");

        res.setStatus("CANCELLED");
        repository.add(res); // 업데이트
        
        System.out.println("🚫 예약 취소됨: " + reservationId);
        return res.getRoomNumber(); 
    }
    
    //[관리자용]모든 예약 목록 조회
    public List<ClientReservation> getAllReservations() {
        return repository.findAll();
    }
}