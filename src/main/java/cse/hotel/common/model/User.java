package cse.hotel.common.model;

import java.io.Serializable;

// Serializable: 네트워크를 통해 객체를 전송하기 위해 필수
public class User implements Serializable {
    private static final long serialVersionUID = 10L; 
    
    private String id;
    private String password;
    private boolean isAdmin; 

    // 생성자
    public User(String id, String password, boolean isAdmin) {
        this.id = id;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    // Getter and Setter (필요에 따라 구현)
    public String getId() { return id; }
    public String getPassword() { return password; }
    public boolean isAdmin() { return isAdmin; }
}