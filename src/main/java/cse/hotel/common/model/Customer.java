package cse.hotel.common.model;

import java.io.Serializable;

public class Customer implements Serializable {
    
    // TCP 통신을 위한 직렬화 ID (경고 방지 및 버전 관리)
    private static final long serialVersionUID = 1L;

    // 1. 고객 고유 아이디 (자동 생성 또는 UUID 사용 권장)
    private String customerId;
    
    // 2. 고객 이름
    private String name;
    
    // 3. 고객 전화번호 (Key 역할을 할 수 있음)
    private String phoneNumber;

    // --- 생성자 ---
    
    public Customer(String customerId, String name, String phoneNumber) {
        this.customerId = customerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }
    
    // --- Getter 및 Setter ---

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // --- 데이터 확인용 toString (선택 사항) ---

//    @Override
//    public String toString() {
//        return "Customer{" +
//                "customerId='" + customerId + '\'' +
//                ", name='" + name + '\'' +
//                ", phoneNumber='" + phoneNumber + '\'' +
//                '}';
//    }
}