package cse.hotel.common.model;

import java.io.Serializable;

public class FoodOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    private String orderId;      // 주문 번호 (자동 생성)
    private String customerId;   // 주문한 고객 ID
    private int roomNumber;      // 배달할 방 번호
    private String foodName;     // 메뉴 이름
    private int count;           // 수량
    private int totalPrice;      // 총 가격

    public FoodOrder(String orderId, String customerId, int roomNumber, String foodName, int count, int totalPrice) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.roomNumber = roomNumber;
        this.foodName = foodName;
        this.count = count;
        this.totalPrice = totalPrice;
    }

    // Getters
    public String getOrderId() { return orderId; }
    public String getCustomerId() { return customerId; }
    public int getRoomNumber() { return roomNumber; }
    public String getFoodName() { return foodName; }
    public int getCount() { return count; }
    public int getTotalPrice() { return totalPrice; }
    
    @Override
    public String toString() {
        return String.format("[%s호] %s %d개 (₩%d)", roomNumber, foodName, count, totalPrice);
    }
}