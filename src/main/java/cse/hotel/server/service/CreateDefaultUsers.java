package cse.hotel.server.service;

import cse.hotel.common.model.User;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CreateDefaultUsers {
    private static final String FILE_NAME = "users.ser";

    public static void main(String[] args) {
        List<User> recoveryList = new ArrayList<>();
        
        // 1. 관리자 계정 (admin, 1234, true) 생성
        User adminUser = new User("admin", "1234", true);
        recoveryList.add(adminUser);
        
        // 2. 파일에 강제 저장 (덮어쓰기)
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(recoveryList);
            System.out.println("✅ users.ser 파일에 관리자 계정이 성공적으로 복구되었습니다.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("❌ 파일 저장 실패!");
        }
    }
}
