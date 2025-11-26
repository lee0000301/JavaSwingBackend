package cse.hotel.common.model;

import java.io.Serializable;

public class Payment implements Serializable {
    private static final long serialVersionUID = 4L; // 필드 변경으로 버전 업!

    private String paymentId;
    private String customerId;
    private int roomNumber;
    
    // ▼▼▼ [추가] 숙박 기간 문자열 (보고서용) ▼▼▼
    private String stayPeriod; 
    // ---------------------------------------

    private String checkInDate;
    private String checkOutDate;
    private String paymentDate; // 결제 일시 (이건 놔둠)
    
    private int stayDays;       // 몇 박인지 (숫자)
    private String foodItems;
    private double roomFee;
    private double foodFee;
    private double totalAmount;
    private String paymentMethod;

    public Payment(String paymentId, String customerId, int roomNumber, 
                   String stayPeriod, // [추가] 생성자 파라미터
                   String checkIn, String checkOut, String paymentDate,
                   int stayDays, String foodItems, 
                   double roomFee, double foodFee, double totalAmount, String method) {
        
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.stayPeriod = stayPeriod; // 저장
        this.checkInDate = checkIn;
        this.checkOutDate = checkOut;
        this.paymentDate = paymentDate;
        this.stayDays = stayDays;
        this.foodItems = foodItems;
        this.roomFee = roomFee;
        this.foodFee = foodFee;
        this.totalAmount = totalAmount;
        this.paymentMethod = method;
    }

    // Getter 추가
    public String getStayPeriod() { return stayPeriod; }

    // ... (나머지 Getters 기존 유지) ...
    public String getPaymentId() { return paymentId; }
    public String getCustomerId() { return customerId; }
    public int getRoomNumber() { return roomNumber; }
    public String getCheckInDate() { return checkInDate; }
    public String getCheckOutDate() { return checkOutDate; }
    public String getPaymentDate() { return paymentDate; }
    public int getStayDays() { return stayDays; }
    public String getFoodItems() { return foodItems; }
    public double getRoomFee() { return roomFee; }
    public double getFoodFee() { return foodFee; }
    public double getTotalAmount() { return totalAmount; }
    public String getPaymentMethod() { return paymentMethod; }
    
    @Override
    public String toString() {
        // toString에도 기간을 표시해주면 확인하기 좋습니다.
        return String.format("[결제] %s (%s, %d박) - 총 %.0f원", customerId, stayPeriod, stayDays, totalAmount);
    }
}