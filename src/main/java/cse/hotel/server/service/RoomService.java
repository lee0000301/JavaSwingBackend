package cse.hotel.server.service;

import cse.hotel.common.exception.DuplicateIdException;
import cse.hotel.common.exception.DataNotFoundException;
import java.util.List;
import cse.hotel.server.repository.RoomRepository;
import cse.hotel.common.model.RoomStatus;
import cse.hotel.common.model.Room;

// RoomStatus Enum이 별도로 정의되었다고 가정합니다.
// public enum RoomStatus { AVAILABLE, OCCUPIED, CLEANING, RESERVED }

public class RoomService {

    // 1. Singleton 인스턴스
    private static final RoomService instance = new RoomService();
    
    // Repository 인스턴스를 가져옵니다 (Singleton 패턴 사용)
    private final RoomRepository roomRepository = RoomRepository.getInstance(); 

    // 2. private 생성자로 외부 생성 차단
    private RoomService() {
        // 서버 시작 시 Repository를 통해 .ser 파일이 로드됩니다.
    }

    // 3. 인스턴스 접근 메서드
    public static RoomService getInstance() {
        return instance;
    }
    
    // --- 객실 상태 변경 로직 (Service) ---
    // (ClientHandler에서 'UPDATE_ROOM' 명령으로 통합 처리될 수 있습니다.)
    
    // Helper 메서드: 상태 변경 로직을 통합하고 예외 처리 도입
    private void changeStatus(int roomNumber, RoomStatus expectedStatus, RoomStatus newStatus) 
            throws DataNotFoundException, IllegalArgumentException {
        
        Room room = roomRepository.findRoomByNumber(roomNumber);
        
        if (room == null) {
            throw new DataNotFoundException("객실 번호 " + roomNumber + "을 찾을 수 없습니다.");
        }
        
        if (room.getStatus() != expectedStatus) {
            throw new IllegalArgumentException("객실 상태 오류: 현재 " + room.getStatus() + " 상태에서는 " + newStatus + "로 변경할 수 없습니다.");
        }
        
        room.setStatus(newStatus);
        
        if (roomRepository.updateRoom(room) == null) {
             // Repository가 null을 반환하면 업데이트 실패로 간주
             throw new DataNotFoundException("객실 정보 업데이트 중 오류가 발생했습니다.");
        }
    }
    
    // 기존 메서드들을 새 로직을 사용하도록 수정 및 예외 처리
    public void checkIn(int roomNumber) throws DataNotFoundException, IllegalArgumentException {
    Room room = roomRepository.findRoomByNumber(roomNumber);
    
    if (room == null) {
        throw new DataNotFoundException("객실 번호 " + roomNumber + "을 찾을 수 없습니다.");
    }

    // [수정됨] '예약됨(RESERVED)' 상태이거나 '빈 방(AVAILABLE)' 상태면 체크인 가능
    if (room.getStatus() == RoomStatus.RESERVED || room.getStatus() == RoomStatus.AVAILABLE) {
        
        room.setStatus(RoomStatus.OCCUPIED); // 입실 완료 상태로 변경
        
        if (roomRepository.updateRoom(room) == null) {
             throw new DataNotFoundException("객실 정보 업데이트 실패");
        }
    } else {
        // 이미 꽉 찼거나(OCCUPIED), 청소 중(CLEANING)인 경우
        throw new IllegalArgumentException("현재 방 상태(" + room.getStatus() + ")에서는 입실할 수 없습니다.");
    }
}
    public void checkOut(int roomNumber) throws DataNotFoundException, IllegalArgumentException {
        changeStatus(roomNumber, RoomStatus.OCCUPIED, RoomStatus.CLEANING);
    }
    public void finishCleaning(int roomNumber) throws DataNotFoundException, IllegalArgumentException {
        changeStatus(roomNumber, RoomStatus.CLEANING, RoomStatus.AVAILABLE);
    }
    public void reserveRoom(int roomNumber) throws DataNotFoundException, IllegalArgumentException {
        changeStatus(roomNumber, RoomStatus.AVAILABLE, RoomStatus.RESERVED);
    }
    
    
    
    // [추가] 예약 취소 시 방을 다시 이용 가능 상태로 변경
    public void cancelBooking(int roomNumber) throws DataNotFoundException {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        if (room == null) return; // 방이 없으면 무시

        // 방 상태를 강제로 AVAILABLE로 복구
        room.setStatus(RoomStatus.AVAILABLE);
        roomRepository.updateRoom(room);
        System.out.println("♻️ 객실 복구 완료: " + roomNumber + "호 -> AVAILABLE");
    }


    // --- UI/Repository 연동 메서드 (ClientHandler 호출용) ---

    // [CREATE] 신규 객실 등록 (ADD_ROOM 명령)
    public void addRoom(Room newRoom) throws DuplicateIdException, IllegalArgumentException {
        if (roomRepository.findRoomByNumber(newRoom.getRoomNumber()) != null) {
             throw new DuplicateIdException("객실 번호 " + newRoom.getRoomNumber() + "은(는) 이미 존재합니다.");
        }
        if (newRoom.getPrice() <= 0) {
             throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        }
        
        // Repository의 addRoom은 성공하면 Room을 반환하므로, null 체크를 통해 예외 처리가 가능하지만,
        // 위에서 중복 체크를 했으므로 바로 호출합니다.
        roomRepository.addRoom(newRoom); 
    }

    // [READ] 모든 객실 목록 조회 (GET_ROOMS 명령)
    public List<Room> getAllRooms() {
        return roomRepository.findAllRooms();
    }
    
    // [UPDATE] 객실 정보 수정 (UPDATE_ROOM 명령)
    public void updateRoom(Room updatedRoom) throws DataNotFoundException, IllegalArgumentException {
        if (roomRepository.findRoomByNumber(updatedRoom.getRoomNumber()) == null) {
            throw new DataNotFoundException("객실 번호 " + updatedRoom.getRoomNumber() + "을(를) 찾을 수 없어 수정할 수 없습니다.");
        }
        if (updatedRoom.getPrice() <= 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 합니다.");
        }
        
        roomRepository.updateRoom(updatedRoom);
    }

    // [DELETE] 객실 삭제 (DELETE_ROOM 명령)
    public void deleteRoom(int roomNumber) throws DataNotFoundException {
        if (!roomRepository.deleteRoom(roomNumber)) {
             throw new DataNotFoundException("객실 번호 " + roomNumber + "을(를) 찾을 수 없어 삭제할 수 없습니다.");
        }
    }
    
    // [READ] 객실 정보 1개 조회 (필요 시)
    public Room getRoomInfo(int roomNumber) {
         return roomRepository.findRoomByNumber(roomNumber);
    }
}