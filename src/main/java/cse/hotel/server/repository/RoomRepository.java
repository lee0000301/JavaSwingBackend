package cse.hotel.server.repository;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import java.io.*;
import cse.hotel.common.model.Room;

/**
 * 객실 데이터를 관리하는 '자바 데이터베이스' (Repository)
 * SFR-402 (등록, 수정, 삭제) 기능 구현
 */
public class RoomRepository {
    
    // 1. Singleton 인스턴스 (명시적 Singleton 패턴 도입)
    private static final RoomRepository instance = new RoomRepository();
    
    // 자바로 만드는 '객실 DB'. static으로 선언해서 모든 곳에서 공유
    private static final Map<Integer, Room> roomDatabase = new ConcurrentHashMap<>();
    
    // 파일 경로 설정
    private static final String FILE_NAME = "data/room_data.ser";
    private static final File DATA_FILE;
    // --- Static 초기화 블록
    static {
        // 3. DATA_FILE 초기화
        DATA_FILE = new File(FILE_NAME);
        
        // 4. 폴더 생성 로직 (NPE 발생 지점 안정화)
        File parentDir = DATA_FILE.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        
        // loadData()는 Constructor에서 호출되도록 유지
        loadData(); 
        
        // 만약 파일에 데이터가 없다면, 기본 예시 데이터를 추가합니다.
        if (roomDatabase.isEmpty()) {
             roomDatabase.put(101, new Room(101, "싱글", 80000));
             System.out.println("기본 예시 객실 데이터 추가됨.");
             saveData(); // 기본 데이터를 파일에 저장
        }
    }

    // 2. private 생성자로 외부 생성 차단
    private RoomRepository() {
    }
    
    // 3. 인스턴스 접근 메서드
    public static RoomRepository getInstance() {
        return instance;
    }

    // 파일에서 데이터 불러오기
    private static void loadData() {
        // DATA_FILE 객체를 사용하고, 파일 존재 및 크기 체크
        if (DATA_FILE.exists() && DATA_FILE.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                @SuppressWarnings("unchecked")
                Map<Integer, Room> loadedMap = (Map<Integer, Room>) ois.readObject();
                roomDatabase.putAll(loadedMap);
                System.out.println("✅ 객실 데이터 로드 성공: " + roomDatabase.size() + "개 항목");
            } catch (FileNotFoundException e) {
                // 이미 exists()로 체크했지만, 만약을 대비
                System.out.println("데이터 파일이 존재하지 않습니다.");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("❌ 데이터 로드 오류: " + e.getMessage());
            }
        } else {
            System.out.println("데이터 파일이 없거나 비어있어 새로 생성합니다.");
        }
    }
    
    //파일에 데이터 저장하기
    private static void saveData() {
        // DATA_FILE 객체를 사용
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(roomDatabase);
            System.out.println("💾 객실 데이터 저장 완료.");
        } catch (IOException e) {
            System.err.println("❌ 데이터 저장 오류: " + e.getMessage());
        }
    }

    /**
     * C: 새 객실 등록 (SFR-402)
     */
    public Room addRoom(Room room) {
        if (roomDatabase.containsKey(room.getRoomNumber())) {
            // 이미 방 번호가 존재하면 등록 실패 (null 반환)
            System.out.println("오류: " + room.getRoomNumber() + "번 객실은 이미 존재합니다.");
            return null;
        }
        roomDatabase.put(room.getRoomNumber(), room);
        saveData(); // <- 저장 호출
        return room;
    }

    /**
     * R: 객실 번호로 1개 조회 (SFR-401)
     */
    public Room findRoomByNumber(int roomNumber) {
        return roomDatabase.get(roomNumber);
    }

    /**
     * R: 모든 객실 목록 조회 (SFR-403)
     */
    public List<Room> findAllRooms() {
        return new ArrayList<>(roomDatabase.values());
    }

    /**
     * U: 객실 정보 수정 (SFR-402)
     */
    public Room updateRoom(Room roomToUpdate) {
        if (!roomDatabase.containsKey(roomToUpdate.getRoomNumber())) {
            System.out.println("오류: " + roomToUpdate.getRoomNumber() + "번 객실이 존재하지 않아 수정할 수 없습니다.");
            return null;
        }
        // 기존 정보를 새 정보(roomToUpdate)로 덮어쓰기
        roomDatabase.put(roomToUpdate.getRoomNumber(), roomToUpdate);
        saveData(); // <- 저장 호출
        return roomToUpdate;
    }

    /**
     * D: 객실 삭제 (SFR-402)
     */
    public boolean deleteRoom(int roomNumber) {
        if (!roomDatabase.containsKey(roomNumber)) {
            System.out.println("오류: " + roomNumber + "번 객실이 존재하지 않아 삭제할 수 없습니다.");
            return false;
        }
        roomDatabase.remove(roomNumber);
        saveData(); // <- 저장 호출
        return true;
    }
}