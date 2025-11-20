package cse.hotel.server.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import cse.hotel.common.model.Food;

public class FoodRepository {

    // 1. 싱글톤 인스턴스
    private static final FoodRepository instance = new FoodRepository();

    // 2. 파일 경로 설정 (프로젝트 루트 폴더 기준)
    private static final String FILE_PATH = "data/fnb_menu.ser"; 
    private final File dataFile = new File(FILE_PATH);

    // 3. private 생성자로 외부 생성 차단
    private FoodRepository() {
        // 데이터 폴더가 없으면 생성
        if (!dataFile.getParentFile().exists()) {
            dataFile.getParentFile().mkdirs();
        }
    }

    // 4. 인스턴스 접근 메서드
    public static FoodRepository getInstance() {
        return instance;
    }

    /**
     * 파일에서 Food 리스트를 불러옵니다. (Load)
     * @return 파일에서 읽어온 Food DTO List (파일이 없거나 비어있으면 빈 List 반환)
     */
    public synchronized List<Food> loadMenus() {
        List<Food> menuList = new ArrayList<>();
        
        // 파일이 존재하고 크기가 0보다 커야 데이터를 읽어옴
        if (dataFile.exists() && dataFile.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
                menuList = (List<Food>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("메뉴 파일 로드 실패: " + e.getMessage());
                // 파일 손상 시에도 빈 리스트 반환하여 프로그램 계속 실행
            }
        }
        return menuList;
    }

    /**
     * 메모리의 Food 리스트를 파일에 저장(덮어쓰기)합니다. (Save)
     * @param menuList 파일에 저장할 전체 메뉴 List
     */
    public synchronized void saveMenus(List<Food> menuList) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            oos.writeObject(menuList);
        } catch (IOException e) {
            System.err.println("메뉴 파일 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
}