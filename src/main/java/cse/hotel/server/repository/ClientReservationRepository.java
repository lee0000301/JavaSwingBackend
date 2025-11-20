package cse.hotel.server.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import cse.hotel.common.model.ClientReservation;

public class ClientReservationRepository {
    
    // ▼▼▼ [수정] 요청하신 파일명으로 변경 (data 폴더 안에 저장) ▼▼▼
    private static final String FILE_PATH = "data/client_reservation.ser";
    
    private static final ClientReservationRepository instance = new ClientReservationRepository();
    private List<ClientReservation> reservationList;

    // 생성자
    private ClientReservationRepository() {
        // data 폴더가 없으면 자동 생성
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        
        this.reservationList = load();
    }

    public static ClientReservationRepository getInstance() {
        return instance;
    }

    // --- CRUD 메서드 ---

    public void add(ClientReservation reservation) {
        // ID가 같은 기존 예약이 있다면 교체(수정), 없으면 추가
        ClientReservation existing = findById(reservation.getReservationId());
        if (existing != null) {
            reservationList.remove(existing);
        }
        
        reservationList.add(reservation);
        save(); // 변경 즉시 파일 저장
    }

    public List<ClientReservation> findAll() {
        return new ArrayList<>(reservationList);
    }

    public ClientReservation findById(String reservationId) {
        for (ClientReservation r : reservationList) {
            if (r.getReservationId() != null && r.getReservationId().equals(reservationId)) {
                return r;
            }
        }
        return null;
    }

    // --- 파일 저장/로드 (I/O) ---

    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(reservationList);
            System.out.println("💾 예약 데이터 저장 완료 (" + FILE_PATH + ")");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ 예약 저장 실패");
        }
    }

    @SuppressWarnings("unchecked")
    private List<ClientReservation> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ClientReservation>) ois.readObject();
        } catch (Exception e) {
            System.out.println("새로운 예약 데이터 파일을 생성합니다.");
            return new ArrayList<>();
        }
    }
}