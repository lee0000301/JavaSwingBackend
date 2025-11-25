package cse.hotel.server.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import cse.hotel.common.model.Reservation;

public class ReservationRepository {

    // 1. 싱글톤 인스턴스
    private static final ReservationRepository instance = new ReservationRepository();

    // 2. 파일 경로 설정 (프로젝트 루트 폴더 기준)
    private static final String FILE_PATH = "data/client_reservation.ser"; 
    private final File dataFile = new File(FILE_PATH);

    // 3. private 생성자로 외부 생성 차단
    private ReservationRepository() {
        // 데이터 폴더가 없으면 생성
        if (!dataFile.getParentFile().exists()) {
            dataFile.getParentFile().mkdirs();
        }
    }

    // 4. 인스턴스 접근 메서드
    public static ReservationRepository getInstance() {
        return instance;
    }

    /**
     * 파일에서 Reservation 리스트를 불러옵니다. (Load)
     * @return 파일에서 읽어온 Reservation DTO List (파일이 없거나 비어있으면 빈 List 반환)
     */
    @SuppressWarnings("unchecked")
    public synchronized List<Reservation> loadReservations() {
        List<Reservation> reservationList = new ArrayList<>();
        
        System.out.println("--- [DEBUG] 예약 파일 로드 시도: " + dataFile.getAbsolutePath() + " ---");
        
        // 파일이 존재하고 크기가 0보다 커야 데이터를 읽어옴
        if (dataFile.exists() && dataFile.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
                reservationList = (List<Reservation>) ois.readObject();
                
                System.out.println("[DEBUG] 로드 성공! 총 " + reservationList.size() + "개의 예약 로드.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("예약 파일 로드 실패: " + e.getMessage());
                // 파일 손상 시 빈 리스트 반환
            }
        }
        return reservationList;
    }

    /**
     * 메모리의 Reservation 리스트를 파일에 저장(덮어쓰기)합니다. (Save)
     * @param reservationList 파일에 저장할 전체 예약 List
     */
    public synchronized void saveReservations(List<Reservation> reservationList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            oos.writeObject(reservationList);
        } catch (IOException e) {
            System.err.println("예약 파일 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}