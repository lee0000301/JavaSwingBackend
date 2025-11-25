package cse.hotel.server.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FoodOrderRepository {
    
    // 저장할 파일명
    private static final String FILE_PATH = "data/food_orders.ser";
    
    private static final FoodOrderRepository instance = new FoodOrderRepository();
    
    // 주문서(Map)들을 모아두는 리스트
    private List<Map<String, Object>> orderList;

    private FoodOrderRepository() {
        // 폴더 없으면 생성
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        // 파일 로드
        this.orderList = load();
    }

    public static FoodOrderRepository getInstance() {
        return instance;
    }

    // --- 주문 추가 (저장) ---
    public void addOrder(Map<String, Object> orderMap) {
        orderList.add(orderMap);
        save(); // 추가하자마자 파일에 저장
    }

    // --- 전체 주문 조회 ---
    public List<Map<String, Object>> findAll() {
        return new ArrayList<>(orderList);
    }

    // --- 파일 쓰기 (직렬화) ---
    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(orderList);
            System.out.println("💾 주문 내역 저장 완료 (" + orderList.size() + "건)");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ 주문 저장 실패");
        }
    }
    public List<Map<String, Object>> getAllOrders() {
        return load();
    }

    // --- 파일 읽기 (역직렬화) ---
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new ArrayList<>(); // 파일 없으면 빈 리스트 시작
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Map<String, Object>>) ois.readObject();
        } catch (Exception e) {
            System.out.println("새로운 주문 장부를 생성합니다.");
            return new ArrayList<>();
        }
    }
}