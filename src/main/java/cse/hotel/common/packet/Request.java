package cse.hotel.common.packet;

import java.io.Serializable;

/**
 * 클라이언트가 서버로 보내는 요청 객체 (Request Packet)
 */
public class Request implements Serializable {
    private static final long serialVersionUID = 100L;

    // 수행할 명령 (예: "ADD_FOOD", "GET_ROOMS", "UPDATE_RESERVATION")
    private String command; 
    
    // 명령 수행에 필요한 데이터 객체 (예: Food 객체, roomNumber int 값, String ID)
    private Object data;    

    // --- 생성자 ---
    
    /**
     * 요청을 생성합니다.
     * @param command 수행할 명령
     * @param data 명령 수행에 필요한 데이터
     */
    public Request(String command, Object data) {
        this.command = command;
        this.data = data;
    }
    
    // --- Getters & Setters ---
    
    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "Request{command='" + command + "', dataClass=" + (data != null ? data.getClass().getSimpleName() : "null") + '}';
    }
}