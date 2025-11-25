package cse.hotel.server.repository;

import cse.hotel.common.model.Food;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FoodRepository {
    
    // 메뉴 데이터 저장 파일
    private static final String FILE_PATH = "data/fnb_menu.ser";
    
    private static final FoodRepository instance = new FoodRepository();
    private List<Food> foodList;

    private FoodRepository() {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        this.foodList = load();
        
        // 초기 메뉴 자동 생성
        if (this.foodList.isEmpty()) {
            addFood(new Food("치킨", 20000, "바삭한 후라이드", 10));
            addFood(new Food("피자", 25000, "치즈 듬뿍", 10));
            addFood(new Food("콜라", 2000, "코카콜라 500ml", 50));
            addFood(new Food("맥주", 5000, "생맥주 500cc", 30));
            save(); 
        }
    }

    public static FoodRepository getInstance() {
        return instance;
    }

    // --- 조회 ---
    public List<Food> findAll() {
        return new ArrayList<>(foodList);
    }

    public Food findByName(String name) {
        for (Food f : foodList) {
            if (f.getName().equals(name)) return f;
        }
        return null;
    }

    // --- 추가 ---
    public void addFood(Food food) {
        // 이름 중복 시 덮어쓰기 혹은 무시 (여기선 추가)
        foodList.add(food);
        save();
    }

    // --- 수정 (재고 업데이트 포함) ---
    public void updateFood(Food updatedFood) {
        for (int i = 0; i < foodList.size(); i++) {
            if (foodList.get(i).getName().equals(updatedFood.getName())) {
                foodList.set(i, updatedFood);
                save(); // 변경 즉시 저장
                return;
            }
        }
    }
    
    // 리스트 전체 업데이트 (재고 차감 시 사용)
    public void updateFoodList(List<Food> newList) {
        this.foodList = newList;
        save();
    }

    // --- 삭제 ---
    public void deleteFood(String foodName) {
        foodList.removeIf(f -> f.getName().equals(foodName));
        save();
    }

    // --- 파일 I/O ---
    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(foodList);
            System.out.println("식음료 메뉴 저장 완료 (" + foodList.size() + "건)");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    private List<Food> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Food>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}