package cse.hotel.server.service;

import cse.hotel.common.model.User;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private static UserService instance = new UserService();
    private Map<String, User> userMap;

    private UserService() {
        userMap = loadUsers();
    }

    public static UserService getInstance() {
        return instance;
    }

    // 파일에서 유저 정보 읽어오기
    @SuppressWarnings("unchecked")
    private Map<String, User> loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.ser"))) {
            System.out.println("유저 데이터를 로드했습니다.");
            return (Map<String, User>) ois.readObject();
        } catch (Exception e) {
            System.out.println("users.ser 파일이 없어 빈 목록으로 시작합니다.");
            return new HashMap<>();
        }
    }

    // 로그인 검증 메서드
    public User login(String id, String pw) {
        if (userMap.containsKey(id)) {
            User user = userMap.get(id);
            if (user.getPassword().equals(pw)) {
                return user; // 로그인 성공 (유저 객체 반환)
            }
        }
        return null; // 로그인 실패
    }
}