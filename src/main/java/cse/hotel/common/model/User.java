package cse.hotel.common.model;

import java.io.Serializable;

// 직렬화를 위해 Serializable 인터페이스 구현
public class User implements Serializable {
    private static final long serialVersionUID = 1L; // 버전 관리용 ID

    private String id;
    private String password;
    private boolean isAdmin; // 관리자 여부

    public User(String id, String password, boolean isAdmin) {
        this.id = id;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // Getter methods
    public String getId() { return id; }
    public String getPassword() { return password; }
    public boolean isAdmin() { return isAdmin; }
    
    @Override
    public String toString() {
        return "User{id='" + id + "', isAdmin=" + isAdmin + "}";
    }
}