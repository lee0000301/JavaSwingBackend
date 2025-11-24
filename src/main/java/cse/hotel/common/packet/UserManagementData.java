package cse.hotel.common.packet;

import cse.hotel.common.model.User;
import java.io.Serializable;
import java.util.List;

/**
 * 사용자 관리 요청/응답에 사용되는 실제 데이터 컨테이너
 * (기존 UserManagementPacket의 필드 역할)
 */
public class UserManagementData implements Serializable {
    private static final long serialVersionUID = 301L;

    public enum Action {
        ADD_USER, DELETE_USER, GET_ALL_USERS
    }
    
    private Action action;          // 수행할 동작 (ADD, DELETE, GET_ALL)
    private User targetUser;        // 추가/삭제 대상 사용자 객체
    private String targetId;        // 삭제할 사용자 ID
    private List<User> userList;    // GET_ALL_USERS 응답 시 사용자 목록

    // --- 생성자 ---
    public UserManagementData(Action action, User targetUser) {
        this.action = action;
        this.targetUser = targetUser;
    }
    
    public UserManagementData(Action action, String targetId) {
        this.action = action;
        this.targetId = targetId;
    }

    public UserManagementData(Action action) {
        this.action = action;
    }
    
    // --- Getters & Setters ---
    public Action getAction() { return action; }
    public User getTargetUser() { return targetUser; }
    public String getTargetId() { return targetId; }
    public List<User> getUserList() { return userList; }
    public void setUserList(List<User> userList) { this.userList = userList; }
}