// 사용자 관리 요청을 담는 기본 패킷
package cse.hotel.common.packet;

import java.io.Serializable;
import java.util.List;
import cse.hotel.common.model.User;

public class UserManagementPacket implements Serializable {
    private static final long serialVersionUID = 11L; 
    
    public enum Action { 
        ADD, DELETE, GET_ALL 
    }
    
    private Action action; // 수행할 동작 (추가, 삭제, 목록 조회)
    private User targetUser; // 추가/삭제 대상 사용자
    private String targetId; // 삭제할 사용자 ID
    private List<User> resultList; // 서버에서 클라이언트로 보낼 결과 목록

    // 1. 사용자 추가 요청용 생성자
    public UserManagementPacket(Action action, User targetUser) {
        this.action = action;
        this.targetUser = targetUser;
    }
    
    // 2. 사용자 삭제 요청용 생성자
    public UserManagementPacket(Action action, String targetId) {
        this.action = action;
        this.targetId = targetId;
    }

    // 3. 목록 조회 요청용 생성자
    public UserManagementPacket(Action action) {
        this.action = action;
    }
    
    // Getter methods
    public Action getAction() { return action; }
    public User getTargetUser() { return targetUser; }
    public String getTargetId() { return targetId; }
    public List<User> getResultList() { return resultList; }
    
    // Setter for result (서버에서 클라이언트로 보낼 때 사용)
    public void setResultList(List<User> resultList) { this.resultList = resultList; }
}