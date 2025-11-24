package cse.hotel.server.service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import cse.hotel.common.model.User;

public class UserManager {
    private static final String FILE_NAME = "users.ser";
    private List<User> userList;

    public UserManager() {
        // 1. 초기화 시 파일에서 사용자 목록을 로드
        userList = loadUsers();
    }

    public List<User> getUserList() {
        return userList;
    }

    // 파일에서 사용자 목록을 불러오는 메서드 (역직렬화)
    @SuppressWarnings("unchecked") // 타입 안전성 경고 무시
    private List<User> loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            // 파일을 성공적으로 읽으면 List<User>로 캐스팅하여 반환
            return (List<User>) ois.readObject(); 
        } catch (FileNotFoundException e) {
            // 파일이 없으면 새로운 리스트를 생성 (최초 실행 시)
            System.out.println("users.ser 파일이 없습니다. 새 리스트를 생성합니다.");
            return new ArrayList<>(); 
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // 사용자 목록을 파일에 저장하는 메서드 (직렬화)
    public void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            // 현재 메모리의 userList 객체를 파일에 씁니다.
            oos.writeObject(userList); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 사용자 추가 및 저장
    public void addUser(User user) {
        userList.add(user);
        saveUsers(); // 변경 후 즉시 파일에 저장
    }

    // 사용자 삭제 및 저장
    public void deleteUser(String userId) {
        userList.removeIf(user -> user.getId().equals(userId));
        saveUsers(); // 변경 후 즉시 파일에 저장
    }
}