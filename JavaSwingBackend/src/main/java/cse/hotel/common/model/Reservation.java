package cse.hotel.common.model;

import java.io.Serializable;

public class Reservation implements Serializable {
    
    // 직렬화 버전 ID (클라이언트와 서버가 이 값이 다르면 에러 발생)
    private static final long serialVersionUID = 1L;

    private String reservationId;   // 예약 고유 번호 (UUID 등)
    private String customerId;      // 고객 ID (또는 이름)
    private int roomNumber;         // 객실 번호 (int)
    private String checkInDate;     // 체크인 날짜 (YYYY-MM-DD)
    private String checkOutDate;    // 체크아웃 날짜
    private String phone;         // 총 결제 금액
    private String status;          // 예약 상태 (예: "예약됨", "체크인", "취소됨")
    private String roomType;        // 객실 타입 (Standard, Deluxe 등 - UI 표시용)

    // 1. 기본 생성자 (필수)
    public Reservation() {
    }

    // 2. 전체 필드 생성자 (편의용)
    public Reservation(String reservationId, String customerId, int roomNumber, 
                       String checkInDate, String checkOutDate, String phone, 
                       String status, String roomType) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.phone = phone;
        this.status = status;
        this.roomType = roomType;
    }

    // --- Getters and Setters ---

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    
    // UI 호환성을 위한 별칭 메소드 (필요하다면 추가)
    public String getReservationNo() {
        return reservationId;
    }
    
    public String getCustomerName() {
        return customerId;
    }
    
    public int getRoomNo() {
        return roomNumber;
    }

    @Override
    public String toString() {
        return "Reservation{" + "id=" + reservationId + ", customer=" + customerId + ", room=" + roomNumber + '}';
    }
}