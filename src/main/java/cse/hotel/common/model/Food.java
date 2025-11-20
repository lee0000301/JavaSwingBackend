package cse.hotel.common.model;

import java.io.Serializable;

public class Food implements Serializable {

    // Java 직렬화를 위한 ID (필수)
    private static final long serialVersionUID = 1L; 

    // 메뉴의 고유 식별자 (SFR-502 등록/수정/삭제의 기준)
    private String menuId; 
    
    // 메뉴 이름
    private String menuName;
    
    // 메뉴 가격 (SFR-503: 가격 관리)
    private int price; 
    
    // 메뉴 재고 (SFR-503: 재고 관리)
    private int stock;
    
    // 메뉴 분류 (예: Food, Beverage, Dessert 등)
    private String category; 

    // --- 생성자 ---
    
    // 기본 생성자 (객체 생성 시 유연성을 위해)
    public Food() {
    }

    // 모든 필드를 초기화하는 생성자 (데이터 설정 편의를 위해)
    public Food(String menuId, String menuName, int price, int stock, String category) {
        this.menuId = menuId;
        this.menuName = menuName;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }

    // --- Getters & Setters ---
    // (필드에 접근하고 값을 변경하기 위한 표준 자바 메서드)

    public String getMenuId() { return menuId; }
    public void setMenuId(String menuId) { this.menuId = menuId; }

    public String getMenuName() { return menuName; }
    public void setMenuName(String menuName) { this.menuName = menuName; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // --- 기타 유틸리티 메서드 (디버깅용) ---

    @Override
    public String toString() {
        return "Food{" +
                "ID='" + menuId + '\'' +
                ", Name='" + menuName + '\'' +
                ", Price=" + price +
                ", Stock=" + stock +
                ", Category='" + category + '\'' +
                '}';
    }
}