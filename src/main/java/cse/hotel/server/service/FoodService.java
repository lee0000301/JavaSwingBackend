package cse.hotel.server.service;

import cse.hotel.common.model.Food;
import cse.hotel.server.repository.FoodRepository;
import cse.hotel.server.repository.FoodOrderRepository; // 주문 내역 저장소
import cse.hotel.server.service.RoomService;
import java.util.List;
import java.util.Map;

public class FoodService {
    
    private static final FoodService instance = new FoodService();
    private final RoomService roomService = RoomService.getInstance();
    
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

  // [수정] 주문 처리 메서드
    public synchronized void processOrder(Map<String, Object> orderMap) throws Exception {
        String foodName = (String) orderMap.get("foodName");
        int count = (int) orderMap.get("count");
        String customerId = (String) orderMap.get("customerId"); // 고객 ID
        int roomNumber = (int) orderMap.get("roomNumber");       // 방 번호

        // ▼▼▼ [추가] 1. 본인 방 검증 로직 ▼▼▼
        if (!roomService.isCheckedIn(roomNumber, customerId)) {
            throw new Exception("고객님께서 체크인하신 객실이 아닙니다.\n(객실 번호를 확인해주세요)");
        }

        // 2. 메뉴 확인 (기존 코드)
        Food food = foodRepository.findByName(foodName);
        if (food == null) throw new Exception("존재하지 않는 메뉴입니다.");

        // 3. 재고 확인 (기존 코드)
        if (food.getStock() < count) throw new Exception("재고 부족");

        // 4. 재고 차감 및 저장 (기존 코드)
        food.decreaseStock(count);
        foodRepository.updateFoodList(foodRepository.findAll());
        orderRepository.addOrder(orderMap);
        
        System.out.println("✅ 검증 완료 및 주문 성공: " + roomNumber + "호 (" + customerId + ")");
    }
}