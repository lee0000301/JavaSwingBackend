package cse.hotel.server; 

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import cse.hotel.server.service.*;


public class HotelServer {
    private static final int PORT = 9999;

    public static void main(String[] args) {
        
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("server is starting. port: " + PORT + "...");
            
            // 서버 시작 시 모든 Service/Repository 초기화 (직렬화 파일 로드)
           // 1. Service 초기화 부분을 try-catch 블록으로 감싸 오류를 출력합니다.
        try {
            RoomService.getInstance(); 
            FoodService.getInstance();
            CustomerService.getInstance();
            System.out.println("server is connected");

        } catch (Throwable t) { // Throwable을 사용하여 모든 종류의 에러(Error, Exception)를 잡습니다.
            System.err.println("error");
            t.printStackTrace(); // 오류의 정확한 위치를 추적하기 위해 전체 스택 트레이스를 출력합니다.
            return; // 오류 발생 시 서버 실행을 중단합니다.
        }
            // (Service의 Singleton 생성자가 Repository를 호출하며 초기화되므로 명시적으로 호출할 필요는 없지만, 안전을 위해 호출할 수도 있습니다.)
            
            while (true) {
                Socket clientSocket = serverSocket.accept(); // 연결 수락
                System.out.println("new client is connected: " + clientSocket.getInetAddress());
                
                // ClientHandler를 새 스레드에서 실행
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ 서버 실행 오류: " + e.getMessage());
        }
    }
}