package cse.hotel.server.service;

import cse.hotel.common.exception.DuplicateIdException;
import cse.hotel.common.exception.DataNotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import cse.hotel.server.repository.FoodRepository;
import cse.hotel.common.model.Food;

public class FoodService {

    // 1. 싱글톤 인스턴스
    private static final FoodService instance = new FoodService();
    
    // FoodRepository 인스턴스를 가져옵니다.
    // FoodRepository가 같은 패키지(com.hotel.project.food)에 있다고 가정합니다.
    private FoodRepository repository = FoodRepository.getInstance();

    // 2. private 생성자로 외부 생성 차단
    private FoodService() {
        // 서버 시작 시 FoodRepository를 통해 .ser 파일이 로드됩니다.
    }

    // 3. 인스턴스 접근 메서드
    public static FoodService getInstance() {
        return instance;
    }

    // --- SFR-502: Read (조회) ---
    
    /**
     * 전체 메뉴 목록을 반환합니다. (GET_FOODS 명령 처리용)
     */
    public List<Food> getAllFoods() { // 메서드 이름 변경 (getAllMenus -> getAllFoods)
        return repository.loadMenus();
    }

    // --- SFR-502: Create (등록) ---

    /**
     * 새로운 메뉴를 등록합니다. (ADD_FOOD 명령 처리용)
     * @throws DuplicateIdException 메뉴 ID가 이미 존재할 경우 발생
     */
    public void addFood(Food newMenu) throws DuplicateIdException { // 메서드 이름 변경 (addMenu -> addFood)
        // 1. 유효성 검사 (가격/재고는 0 이상이어야 함)
        if (newMenu.getPrice() <= 0 || newMenu.getStock() < 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 하며, 재고는 음수일 수 없습니다.");
        }
        
        List<Food> menuList = repository.loadMenus();
        
        // 2. ID 중복 검사
        boolean idExists = menuList.stream()
                .anyMatch(menu -> menu.getMenuId().equals(newMenu.getMenuId()));

        if (idExists) {
            throw new DuplicateIdException("오류: 이미 등록된 메뉴 ID입니다. (" + newMenu.getMenuId() + ")");
        }
        
        // 3. 리스트에 추가 및 파일 저장
        menuList.add(newMenu);
        repository.saveMenus(menuList);
    }

    // --- SFR-502: Update (수정) ---

    /**
     * 기존 메뉴 정보를 수정합니다. (UPDATE_FOOD 명령 처리용)
     * @throws DataNotFoundException 해당 메뉴 ID가 존재하지 않을 경우 발생
     */
    public void updateFood(Food updatedMenu) throws DataNotFoundException { // 메서드 이름 변경 (updateMenu -> updateFood)
        // 1. 유효성 검사 (가격/재고는 0 이상이어야 함)
        if (updatedMenu.getPrice() <= 0 || updatedMenu.getStock() < 0) {
            throw new IllegalArgumentException("가격은 0보다 커야 하며, 재고는 음수일 수 없습니다.");
        }

        List<Food> menuList = repository.loadMenus();
        boolean found = false;
        
        for (int i = 0; i < menuList.size(); i++) {
            if (menuList.get(i).getMenuId().equals(updatedMenu.getMenuId())) {
                menuList.set(i, updatedMenu); // 기존 객체를 새 객체로 대체
                found = true;
                break;
            }
        }
        
        if (!found) {
            throw new DataNotFoundException("오류: 수정하려는 메뉴 ID를 찾을 수 없습니다.");
        }
        
        // 2. 리스트 변경 사항 파일에 저장
        repository.saveMenus(menuList);
    }
    
    // --- SFR-502: Delete (삭제) ---

    /**
     * 특정 메뉴 ID를 가진 메뉴를 삭제합니다. (DELETE_FOOD 명령 처리용)
     * @throws DataNotFoundException 해당 메뉴 ID가 존재하지 않을 경우 발생
     */
    public void deleteFood(String menuId) throws DataNotFoundException { // 메서드 이름 변경 (deleteMenu -> deleteFood)
        List<Food> menuList = repository.loadMenus();
        int originalSize = menuList.size();
        
        // Stream API를 사용하여 해당 ID를 제외한 새 리스트 생성
        List<Food> newList = menuList.stream()
                .filter(menu -> !menu.getMenuId().equals(menuId))
                .collect(Collectors.toList());

        // 사이즈가 줄어들지 않았다면 해당 ID가 존재하지 않음
        if (newList.size() == originalSize) {
            throw new DataNotFoundException("오류: 삭제하려는 메뉴 ID를 찾을 수 없습니다.");
        }
        
        // 삭제된 리스트를 파일에 저장
        repository.saveMenus(newList);
    }
}