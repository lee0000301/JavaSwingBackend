package cse.hotel.server.service;

import cse.hotel.common.model.Room;
import cse.hotel.common.model.RoomStatus; // Enum import 필수
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

    // --- [핵심] 체크인 (검증 포함) ---
    public void checkInWithValidation(int roomNumber, String customerId) throws Exception {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        if (room == null) throw new DataNotFoundException("객실 없음");

        // 1. 예약 내역 확인
        boolean isMyReservation = false;
        List<ClientReservation> myList = resService.getReservationsByCustomerId(customerId);
        for (ClientReservation r : myList) {
            if (r.getRoomNumber() == roomNumber && "CONFIRMED".equals(r.getStatus())) {
                isMyReservation = true; break;
            }
        }
        if (!isMyReservation) throw new Exception("예약된 객실이 아닙니다.");

        // 2. 실제 체크인 실행
        checkIn(roomNumber);
    }

    public void checkIn(int roomNumber) throws Exception {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        
        // ▼▼▼ [수정] Enum 비교 로직 ▼▼▼
        if (room.getStatus() == RoomStatus.OCCUPIED) {
            throw new Exception("이미 입실 완료된 객실입니다.");
        }

        // 예약됨(RESERVED) 또는 빈 방(AVAILABLE)일 때만 입실 가능
        if (room.getStatus() == RoomStatus.RESERVED || room.getStatus() == RoomStatus.AVAILABLE) {
            room.setStatus(RoomStatus.OCCUPIED); // 상태 변경
            roomRepository.updateRoom(room);
            System.out.println("✅ 체크인 완료: " + roomNumber);
        } else {
            throw new Exception("입실 불가능한 상태입니다: " + room.getStatus());
        }
    }

    // --- 체크아웃 ---
    public void checkOut(int roomNumber) throws Exception {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        if (room == null) throw new DataNotFoundException("객실 없음");

        if (room.getStatus() == RoomStatus.OCCUPIED) {
            room.setStatus(RoomStatus.CLEANING); // 청소중으로 변경
            roomRepository.updateRoom(room);
            System.out.println("👋 체크아웃 완료: " + roomNumber);
        } else {
            throw new Exception("체크아웃 가능한 상태가 아닙니다.");
        }
    }

    public void checkOutWithValidation(int roomNumber, String customerId) throws Exception {
        checkOut(roomNumber);
    }

    // --- 기타 상태 변경 ---
    public void finishCleaning(int roomNumber) {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        if (room != null) {
            room.setStatus(RoomStatus.AVAILABLE); // 빈 객실로
            roomRepository.updateRoom(room);
        }
    }

    public void reserveRoom(int roomNumber) {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        if (room != null) {
            room.setStatus(RoomStatus.RESERVED); // 예약됨으로
            roomRepository.updateRoom(room);
        }
    }

    public void cancelBooking(int roomNumber) {
        Room room = roomRepository.findRoomByNumber(roomNumber);
        if (room != null) {
            room.setStatus(RoomStatus.AVAILABLE); // 빈 객실로 복구
            roomRepository.updateRoom(room);
        }
    }

    // --- 기본 CRUD ---
    public List<Room> getAllRooms() { return roomRepository.findAllRooms(); }
    
    public void addRoom(Room room) throws DuplicateIdException {
        if (roomRepository.findRoomByNumber(room.getRoomNumber()) != null) 
            throw new DuplicateIdException("중복된 방 번호");
        roomRepository.addRoom(room);
    }
    
    public void updateRoom(Room room) { roomRepository.updateRoom(room); }
    public void deleteRoom(int roomNumber) { roomRepository.deleteRoom(roomNumber); }
    public Room getRoomInfo(int roomNumber) { return roomRepository.findRoomByNumber(roomNumber); }
}