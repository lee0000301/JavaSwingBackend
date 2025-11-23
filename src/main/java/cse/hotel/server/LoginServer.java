package cse.hotel.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import cse.hotel.common.model.User;

public class LoginServer {
    private static final int PORT = 5000;
    private static HashMap<String, User> userDatabase;

    public static void main(String[] args) {
        // 1. 서버 시작 시 .ser 파일 로드
        loadUserData();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("서버가 시작되었습니다. 포트: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("클라이언트 연결됨: " + clientSocket.getInetAddress());

                // 각 클라이언트 처리를 위한 스레드 실행 (생략 가능하나 다중 접속 시 권장)
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 파일에서 유저 정보 읽어오기
    @SuppressWarnings("unchecked")
    private static void loadUserData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.ser"))) {
            userDatabase = (HashMap<String, User>) ois.readObject();
            System.out.println("유저 데이터 로드 완료: " + userDatabase.size() + "명");
        } catch (Exception e) {
            System.out.println("데이터 파일을 찾을 수 없습니다. UserDataGenerator를 먼저 실행하세요.");
            userDatabase = new HashMap<>(); // 빈 맵으로 초기화
        }
    }

    private static void handleClient(Socket socket) {
        try (
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            // 2. 클라이언트로부터 ID, PW 수신
            String inputId = (String) in.readObject();
            String inputPw = (String) in.readObject();
            System.out.println("로그인 시도 - ID: " + inputId);

            // 3. 검증 로직
            boolean isLoginSuccess = false;
            boolean isAdmin = false;
            String message = "";

            if (userDatabase.containsKey(inputId)) {
                User user = userDatabase.get(inputId);
                if (user.getPassword().equals(inputPw)) {
                    isLoginSuccess = true;
                    isAdmin = user.isAdmin();
                    message = "로그인 성공";
                } else {
                    message = "비밀번호가 틀렸습니다.";
                }
            } else {
                message = "존재하지 않는 아이디입니다.";
            }

            // 4. 클라이언트로 결과 전송
            out.writeBoolean(isLoginSuccess); // 성공 여부
            out.writeBoolean(isAdmin);        // 관리자 여부
            out.writeObject(message);         // 메시지
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}