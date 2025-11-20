package cse.hotel.common.packet;

import java.io.Serializable;

/**
 * 서버가 클라이언트로 보내는 응답 객체 (Response Packet)
 */
public class Response implements Serializable {
    private static final long serialVersionUID = 200L;

    // 요청 처리 성공 여부 (true: 성공, false: 실패)
    private boolean success; 
    
    // 요청 결과로 반환되는 데이터 (예: 조회된 Room 목록 List, 단일 Food 객체)
    private Object resultData; 
    
    // 실패 시 오류 메시지 또는 성공 시 간단한 메시지
    private String message;  

    // --- 생성자 ---
    
    /**
     * 성공적인 응답을 생성합니다.
     * @param resultData 클라이언트에 전달할 결과 데이터
     * @param message 성공 메시지
     */
    public Response(Object resultData, String message) {
        this.success = true;
        this.resultData = resultData;
        this.message = message;
    }
    
    /**
     * 실패 응답을 생성합니다.
     * @param message 실패/오류 메시지
     */
    public Response(String message) {
        this.success = false;
        this.resultData = null; // 실패 시 데이터는 null
        this.message = message;
    }
    
    // --- Getters & Setters ---
    
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getResultData() {
        return resultData;
    }

    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}