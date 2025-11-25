// cse.hotel.server.service.UserService.java (수정)
package cse.hotel.server.service;

import cse.hotel.common.model.User;
import cse.hotel.common.packet.Request;
import cse.hotel.common.packet.Response;
import cse.hotel.common.packet.UserManagementData;
import cse.hotel.server.repository.UserRepository;
import java.util.List;

public class UserService {
    private final UserRepository userRepository;
    private static UserService instance;
    
    private UserService() {
        this.userRepository = new UserRepository();
    }
    
    public static UserService getInstance() {
        if (instance == null) {
            instance = new UserService();
        }
        return instance;
    }
    
    public User login(String id, String password) {
        List<User> users = userRepository.loadAll(); // UserRepository에서 데이터 로드

        for (User user : users) {
            if (user.getId().equals(id) && user.getPassword().equals(password)) {
                return user; // 인증 성공
            }
        }
        return null; // 인증 실패
    }
    
    // 서버의 중앙 처리기에서 호출될 메서드
    public Response processUserRequest(Request request) {
        // 1. data 필드를 UserManagementData로 안전하게 캐스팅
        if (!(request.getData() instanceof UserManagementData)) {
            return new Response("요청 데이터 타입 오류: UserManagementData가 필요합니다.");
        }
        UserManagementData data = (UserManagementData) request.getData();
        
        // 2. UserRepository에서 현재 사용자 목록 로드
        List<User> users = userRepository.loadAll();
        
        switch (data.getAction()) {
            case ADD_USER:
                // 중복 ID 확인 로직 추가 가능
                users.add(data.getTargetUser());
                userRepository.saveAll(users);
                return new Response(null, "사용자가 성공적으로 추가되었습니다.");

            case DELETE_USER:
                boolean removed = users.removeIf(user -> user.getId().equals(data.getTargetId()));
                if (removed) {
                    userRepository.saveAll(users);
                    return new Response(null, "사용자가 성공적으로 삭제되었습니다.");
                } else {
                    return new Response("삭제할 사용자 ID를 찾을 수 없습니다.");
                }

            case GET_ALL_USERS:
                // 결과를 UserManagementData에 담아 Response의 resultData로 반환
                data.setUserList(users);
                return new Response(data, "사용자 목록을 성공적으로 불러왔습니다.");
                
            default:
                return new Response("알 수 없는 사용자 관리 명령입니다.");
        }
    }
}