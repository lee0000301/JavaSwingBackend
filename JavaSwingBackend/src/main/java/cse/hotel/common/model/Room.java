
package cse.hotel.common.model;

import java.io.Serializable;

public class Room implements Serializable{
    
    // 직렬화
    private static final long serialVersionUID = 1L;

    // 1. 필드 (객실의 속성들)
    private int roomNumber;    // 객실 번호 (예: 301)
    private String roomType;   // 객실 타입 (예: "싱글", "더블")
    private int price;         // 가격
    private RoomStatus status; // 객실 상태 (Enum 사용)
    

    // 2. 생성자 (Room 객체를 생성할 때 사용할 '틀')
    public Room(int roomNumber, String roomType, int price) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        // 새 객실은 기본적으로 '이용 가능(AVAILABLE)' 상태로 시작
        this.status = RoomStatus.AVAILABLE; 
    }

    // 3. 게터(Getter)와 세터(Setter) 메서드
    // private 필드에 안전하게 접근하고 값을 변경하기 위한 통로입니다.

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public RoomStatus getStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }

    // 4. (선택사항) toString() 메서드 - 디버깅에 매우 유용합니다!
    // 객체 정보를 한눈에 보고 싶을 때 사용합니다.
    @Override
    public String toString() {
        return "Room [번호=" + roomNumber + 
               ", 타입=" + roomType + 
               ", 가격=" + price + 
               ", 상태=" + status + "]";
    }
}