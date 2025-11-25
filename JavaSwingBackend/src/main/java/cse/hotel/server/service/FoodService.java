package cse.hotel.server.service;

import cse.hotel.common.model.Food;
import cse.hotel.server.repository.FoodRepository;
import cse.hotel.server.repository.FoodOrderRepository; // 주문 내역 저장소
import java.util.List;
import java.util.Map;

public class FoodService {
    
    private static final FoodService instance = new FoodService();
    
    // 두 개의 저장소를 모두 사용합니다.
    private final FoodRepository foodRepository = FoodRepository.getInstance();
    private final FoodOrderRepository orderRepository = FoodOrderRepository.getInstance();

    private FoodService() {}

    public static FoodService getInstance() {
        return instance;
    }

    // --- 기본 CRUD (관리자용) ---
    public List<Food> getAllFoods() {
        return foodRepository.findAll();
    }

    public void addFood(Food food) {
        foodRepository.addFood(food);
    }

    public void updateFood(Food food) {
        foodRepository.updateFood(food);
    }

    public void deleteFood(String foodName) {
        foodRepository.deleteFood(foodName);
    }

    // --- [핵심] 주문 처리 로직 (동기화 필수) ---
    public synchronized void processOrder(Map<String, Object> orderMap) throws Exception {
        String foodName = (String) orderMap.get("foodName");
        int count = (int) orderMap.get("count");

        // 1. 메뉴가 실제로 존재하는지 확인
        Food food = foodRepository.findByName(foodName);
        if (food == null) {
            throw new Exception("존재하지 않는 메뉴입니다: " + foodName);
        }

        // 2. 재고 확인
        if (food.getStock() < count) {
            throw new Exception("재고가 부족합니다. (남은 수량: " + food.getStock() + ")");
        }

        // 3. 재고 차감 (메모리 상에서)
        food.decreaseStock(count);

        // 4. 차감된 재고를 파일(fnb_menu.ser)에 반영
        // (findByName으로 가져온 객체는 리스트 내의 객체와 동일한 참조이므로, save만 호출하면 됨)
        // 안전하게 리스트 전체 업데이트 호출
        foodRepository.updateFoodList(foodRepository.findAll());

        // 5. 주문 내역을 주문 장부 파일(food_orders.ser)에 저장
        orderRepository.addOrder(orderMap);
        
        System.out.println("✅ 룸서비스 주문 처리 완료: " + foodName + " -" + count + "개");
    }
}