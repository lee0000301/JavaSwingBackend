package cse.hotel.server.service;

import cse.hotel.server.repository.FoodOrderRepository;
import java.util.List;
import java.util.Map;

public class FoodOrderService {

    private static final FoodOrderService instance = new FoodOrderService();
    private final FoodOrderRepository repository = FoodOrderRepository.getInstance();

    private FoodOrderService() {}
    public static FoodOrderService getInstance() { return instance; }

    /**
     * 모든 식음료 주문의 총 매출액을 계산합니다.
     * (SFR-907: 식음료 매출 조회)
     */
    public int calculateTotalFnbRevenue() {
        int totalRevenue = 0;
        
        // Repository에서 데이터를 Map 리스트 형태로 가져옵니다.
        // (Repository에 public List<Map<String, Object>> getAllOrders() 메서드가 있다고 가정)
        List<Map<String, Object>> orders = repository.getAllOrders(); 

        if (orders != null) {
            for (Map<String, Object> orderMap : orders) {
                try {
                    // FoodOrder 모델의 "totalPrice" 키를 사용하여 금액을 가져옴
                    Object priceObj = orderMap.get("totalPrice");
                    
                    if (priceObj instanceof Integer) {
                        totalRevenue += (Integer) priceObj;
                    } else if (priceObj instanceof String) {
                        totalRevenue += Integer.parseInt((String) priceObj);
                    }
                } catch (Exception e) {
                    System.err.println("식음료 매출 계산 중 오류(데이터 포맷): " + e.getMessage());
                }
            }
        }
        return totalRevenue;
    }
}