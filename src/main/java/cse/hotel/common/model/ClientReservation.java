package cse.hotel.common.model;

import java.io.Serializable;

public class ClientReservation implements Serializable {
    private static final long serialVersionUID = 3L;

    // 1. 고유 정보
    private String reservationId;       // 예약 번호 (PK, UUID로 자동 생성)
    private String customerId;          // 예약 고객 ID (Customer 엔티티와 연동)
    private int roomNumber;             // 예약 객실 번호 (Room 엔티티와 연동)

    // 2. 시간 정보
    private String checkInDate;         // 체크인 예정일 (YYYY-MM-DD)
    private String checkOutDate;        // 체크아웃 예정일 (YYYY-MM-DD)

    // 3. 결제 및 상태
    private double totalPrice;          // 최종 결제 금액
    private String status;              // 예약 상태 (예: CONFIRMED, CANCELLED)
    
    // --- 생성자 ---
    
    public ClientReservation(String reservationId, String customerId, int roomNumber, String checkInDate, String checkOutDate, double totalPrice, String status) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }
    
    // 기본 생성자 (직렬화/역직렬화 및 DTO 사용에 필수)
    public ClientReservation() {}
    
    // --- Getter 및 Setter --- (생략)
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public int getRoomNumber() { return roomNumber; }
    public void setRoomNumber(int roomNumber) { this.roomNumber = roomNumber; }
    public String getCheckInDate() { return checkInDate; }
    public void setCheckInDate(String checkInDate) { this.checkInDate = checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public void setCheckOutDate(String checkOutDate) { this.checkOutDate = checkOutDate; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "ClientReservation{" + "reservationId='" + reservationId + '\'' + ", customerId=" + customerId + ", roomNumber=" + roomNumber + ", status=" + status + '}';
    }
}