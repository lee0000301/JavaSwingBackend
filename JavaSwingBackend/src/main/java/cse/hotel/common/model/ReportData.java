package cse.hotel.common.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 보고서 생성 결과를 담는 데이터 모델 (SFR-901, SFR-902)
 */
public class ReportData implements Serializable {
    private static final long serialVersionUID = 1L;

    // 1. 핵심 지표 (KPI)
    private double occupancyRate;      // 객실 점유율 (%)
    private double reservationRate;    // 객실 예약률 (%)
    private double totalRevenue;       // 총 매출 (객실 + 식음료)
    private double roomRevenue;        // 객실 매출
    private double fnbRevenue;         // 식음료 매출

    // 2. 기간별 상세 데이터 (SFR-903)
    // Key: 기간 (YYYY-MM-DD 또는 YYYY-MM), Value: 해당 기간의 매출/점유율 등 상세 지표 맵
    private List<Map<String, Object>> periodDetails;

    // 3. 예외 보고서 정보 (SFR-905)
    private String exceptionReportDetails;

    // --- Constructor ---
    public ReportData() {
        // 기본 생성자
    }

    // --- Getters and Setters ---

    public double getOccupancyRate() { return occupancyRate; }
    public void setOccupancyRate(double occupancyRate) { this.occupancyRate = occupancyRate; }

    public double getReservationRate() { return reservationRate; }
    public void setReservationRate(double reservationRate) { this.reservationRate = reservationRate; }

    public double getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(double totalRevenue) { this.totalRevenue = totalRevenue; }

    public double getRoomRevenue() { return roomRevenue; }
    public void setRoomRevenue(double roomRevenue) { this.roomRevenue = roomRevenue; }

    public double getFnbRevenue() { return fnbRevenue; }
    public void setFnbRevenue(double fnbRevenue) { this.fnbRevenue = fnbRevenue; }

    public List<Map<String, Object>> getPeriodDetails() { return periodDetails; }
    public void setPeriodDetails(List<Map<String, Object>> periodDetails) { this.periodDetails = periodDetails; }

    public String getExceptionReportDetails() { return exceptionReportDetails; }
    public void setExceptionReportDetails(String exceptionReportDetails) { this.exceptionReportDetails = exceptionReportDetails; }
}