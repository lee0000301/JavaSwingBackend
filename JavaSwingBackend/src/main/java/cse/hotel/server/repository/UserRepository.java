package cse.hotel.server.repository;

import cse.hotel.common.model.User;
import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class UserRepository {
    private static final String FILE_NAME = "users.ser";

    // 파일에서 사용자 목록을 로드 (직렬화)
    @SuppressWarnings("unchecked")
    public List<User> loadAll() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<User>) ois.readObject(); 
        } catch (FileNotFoundException e) {
            System.out.println("users.ser 파일이 없습니다. 새 리스트를 생성합니다.");
            return initializeDefaultUsers();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("사용자 파일 로드 중 오류: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private List<User> initializeDefaultUsers() {
        List<User> initialUsers = new ArrayList<>();
        
        // 1. 관리자 계정 생성 (id: admin, password: 1234, isAdmin: true)
        User adminUser = new User("admin", "1234", true);
        initialUsers.add(adminUser);
        
        // 2. 초기 사용자 목록을 파일에 즉시 저장
        saveAll(initialUsers); 
        
        return initialUsers;
    }

    // 사용자 목록을 파일에 저장 (직렬화)
    public void saveAll(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(users); 
        } catch (IOException e) {
            System.err.println("사용자 파일 저장 중 오류: " + e.getMessage());
        }
    }
}