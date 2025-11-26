package cse.hotel.common.model;

import java.io.Serializable;

public class Food implements Serializable {
    private static final long serialVersionUID = 2L; // 필드가 바껴서 버전을 올림

    private String name;
    private int price;
    private String description;
    private int stock; // [추가] 재고 수량

    // 생성자
    public Food(String name, int price, String description, int stock) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
    }

    // Getter & Setter
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getDescription() { return description; }
    
    public int getStock() { return stock; } // [추가]
    public void setStock(int stock) { this.stock = stock; } // [추가]

    // 재고 차감 메서드
    public void decreaseStock(int amount) {
        this.stock -= amount;
    }
}