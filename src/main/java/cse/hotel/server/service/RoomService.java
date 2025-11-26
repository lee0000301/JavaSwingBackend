package cse.hotel.server.service;

import cse.hotel.common.model.Room;
import cse.hotel.common.model.RoomStatus; // Enum Import 필수
import cse.hotel.common.model.ClientReservation;
import cse.hotel.server.repository.RoomRepository;
import cse.hotel.common.exception.DataNotFoundException;
import cse.hotel.common.exception.DuplicateIdException;
import java.util.List;

public class RoomService {

    private static final RoomService instance = new RoomService();
    private final RoomRepository roomRepository = RoomRepository.getInstance();
    private final ClientReservationService resService = ClientReservationService.getInstance();

    private RoomService() {}
    public static RoomService getInstance() { return instance; }

    // --- [체크인] ---
    public void checkIn(int roomNumber) throws Exception {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        
        // 1. 이미 점유중인지 확인 (Enum 비교)
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new Exception("이미 입실 완료된 객실입니다.");
        }

        // 2. 예약된 방(RESERVED) 또는 빈 방(AVAILABLE)일 때만 입실 가능
        if (room.getStatus() == RoomStatus.RESERVED || 
            room.getStatus() == RoomStatus.AVAILABLE) {
            
            // ▼▼▼ [수정] String이 아니라 Enum 상수를 직접 넣습니다! ▼▼▼
            room.setStatus(RoomStatus.OCCUPIED); 
            
            roomRepository.updateRoom(room);
            
            // 예약 상태도 변경
            resService.updateReservationStatus(roomNumber, "CHECKED_IN"); 

            System.out.println("✅ 체크인 완료: " + roomNumber + " (상태: OCCUPIED)");
        } else {
            throw new Exception("체크인 불가능한 상태입니다. (현재: " + room.getStatus() + ")");
        }
    }

    // --- [체크아웃] ---
    public void checkOut(int roomNumber) throws Exception {
        Room room = roomRepository.findRoomByNumber(roomNumber);

        // 점유중(OCCUPIED)일 때만 체크아웃 가능
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            
            // ▼▼▼ [수정] Enum 상수 사용 ▼▼▼
            room.setStatus(RoomStatus.CLEANING); // 청소중으로 변경
            
            roomRepository.updateRoom(room);
            
            resService.updateReservationStatus(roomNumber, "COMPLETED");
            
            System.out.println("👋 체크아웃 완료: " + roomNumber + " (상태: CLEANING)");
        } else {
            throw new Exception("체크아웃 가능한 상태가 아닙니다.");
        }
    }

    // --- [청소 완료] ---
    public void finishCleaning(int roomNumber) {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        if (room != null) {
            room.setStatus(RoomStatus.AVAILABLE); // 빈 객실
            roomRepository.updateRoom(room);
        }
    }

    // --- [예약 확정] ---
    public void reserveRoom(int roomNumber) {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        if (room != null) {
            room.setStatus(RoomStatus.RESERVED); // 예약됨
            roomRepository.updateRoom(room);
        }
    }

    // --- [예약 취소] ---
    public void cancelBooking(int roomNumber) {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        if (room != null) {
            room.setStatus(RoomStatus.AVAILABLE); // 빈 객실
            roomRepository.updateRoom(room);
        }
    }
    
    // --- 검증 로직 ---
    public void checkInWithValidation(int roomNumber, String customerId) throws Exception {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        if (room == null) throw new DataNotFoundException("객실 없음");

        boolean isMyReservation = false;
        List<ClientReservation> myList = resService.getReservationsByCustomerId(customerId);
        for (ClientReservation r : myList) {
            // 방 번호가 같고, 예약 상태가 유효한지 확인
            if (r.getRoomNumber() == roomNumber && "CONFIRMED".equals(r.getStatus())) {
                isMyReservation = true; break;
            }
        }
        if (!isMyReservation) throw new Exception("예약 내역이 없습니다.");
        checkIn(roomNumber);
    }
    
    public void checkOutWithValidation(int roomNumber, String customerId) throws Exception {
        checkOut(roomNumber);
    }
    
    // ▼▼▼ [추가] 룸서비스 주문 시 본인 확인 검증용 ▼▼▼
    public boolean isCheckedIn(int roomNumber, String customerId) {
        // 1. 방 정보 확인
        Room room = roomRepository.findRoomByNumber(roomNumber);
        if (room == null) return false;

        // 2. 방 상태가 '점유중(OCCUPIED)'인지 확인 (Enum 비교)
        if (room.getStatus() != RoomStatus.OCCUPIED) {
            // (엄격하게 하려면 OCCUPIED만 허용, 테스트 편의상 RESERVED도 허용 가능)
            return false; 
        }

        // 3. 예약 내역 대조 (이 방의 주인이 맞는지)
        List<ClientReservation> reservations = resService.getReservationsByCustomerId(customerId);
        for (ClientReservation r : reservations) {
            // 방 번호가 일치하면 통과 (체크인 후 상태가 CHECKED_IN 등으로 바뀌었어도 기록은 남음)
            if (r.getRoomNumber() == roomNumber) {
                return true;
            }
        }
        return false;
    }
    
    
    

    
    
    // --- CRUD ---
    public List<Room> getAllRooms() { return roomRepository.findAllRooms(); }
    public void addRoom(Room room) throws DuplicateIdException { roomRepository.addRoom(room); }
    public void updateRoom(Room room) { roomRepository.updateRoom(room); }
    public void deleteRoom(int roomNumber) { roomRepository.deleteRoom(roomNumber); }
    public Room getRoomInfo(int roomNumber) { return roomRepository.findRoomByNumber(roomNumber); }
}