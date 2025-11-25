package cse.hotel.server.service;

import cse.hotel.common.model.ReportData;
import cse.hotel.common.model.ClientReservation; // ClientReservation 사용
import cse.hotel.common.model.Room;
import cse.hotel.server.repository.ReservationRepository;
import cse.hotel.server.repository.RoomRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 호텔 통합 보고서 생성 서비스 (SFR-901 ~ 907)
 * (수정됨: 날짜 기간 자동 계산 및 기간 내 매출 필터링 적용)
 */
public class ReportService {

    private static final ReportService instance = new ReportService();

    private final ReservationRepository reservationRepository = ReservationRepository.getInstance();
    private final RoomRepository roomRepository = RoomRepository.getInstance();
    private final FoodOrderService foodOrderService = FoodOrderService.getInstance();

    private ReportService() {}
    public static ReportService getInstance() { return instance; }

    public ReportData generateReport(Map<String, Object> data) throws Exception {
        // 파라미터 안전하게 추출
        Object startDateObj = data.get("startDate");
        Object endDateObj = data.get("endDate");
        String periodType = (String) data.get("periodType");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startDateStr = (startDateObj instanceof Date) ? sdf.format((Date)startDateObj) : (String)startDateObj;
        String endDateStr = (endDateObj instanceof Date) ? sdf.format((Date)endDateObj) : (String)endDateObj;

        // 1. 데이터 로드
        @SuppressWarnings("unchecked")
        List<ClientReservation> allReservations = (List<ClientReservation>)(List<?>) reservationRepository.loadReservations();
        List<Room> allRooms = roomRepository.findAllRooms();
        
        if (allReservations == null) allReservations = new ArrayList<>();
        if (allRooms == null) allRooms = new ArrayList<>();

        // 2. [중요] 선택한 기간에 해당하는 예약만 필터링
        List<ClientReservation> targetReservations = filterReservationsByDate(allReservations, startDateStr, endDateStr);

        ReportData report = new ReportData();

        // 3. 통계 계산 (필터링된 리스트 사용)
        calculateFinancialMetrics(report, targetReservations);
        calculateOccupancyMetrics(report, allRooms); // 점유율은 현재 상태 기준 (단순화)

        // 4. 상세 데이터 생성 (기간 자동 계산)
        // 기간 차이(일수) 계산
        long diffInMillies = sdf.parse(endDateStr).getTime() - sdf.parse(startDateStr).getTime();
        int dayCount = (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
        
        // 최대 31일까지만 표에 표시 (너무 길어짐 방지, 필요시 제거 가능)
        if(dayCount > 31) dayCount = 31; 

        report.setPeriodDetails(generatePeriodDetails(startDateStr, dayCount, periodType, report, targetReservations));
        
        // 5. 예외 보고서
        report.setExceptionReportDetails(generateExceptionReport());

        return report;
    }

    // --- 날짜 필터링 메서드 ---
    private List<ClientReservation> filterReservationsByDate(List<ClientReservation> all, String start, String end) {
        List<ClientReservation> filtered = new ArrayList<>();
        // 단순 문자열 비교 (YYYY-MM-DD 포맷이므로 가능)
        // 실제로는 start <= checkIn <= end 조건을 체크
        for (ClientReservation r : all) {
            String checkIn = r.getCheckInDate();
            if (checkIn != null && checkIn.compareTo(start) >= 0 && checkIn.compareTo(end) <= 0) {
                filtered.add(r);
            }
        }
        return filtered;
    }

    private void calculateFinancialMetrics(ReportData report, List<ClientReservation> reservations) {
        double totalRoomRevenue = 0;

        for (ClientReservation res : reservations) {
            String status = res.getStatus();
            if (status != null && !status.equals("CANCELLED") && !status.equals("CANCELED")) {
                try {
                    double priceStr = res.getTotalPrice();
                } catch (NumberFormatException e) { }
            }
        }

        double totalFnbRevenue = foodOrderService.calculateTotalFnbRevenue(); // (전체 기간 합산)
        double totalRevenue = totalRoomRevenue + totalFnbRevenue;

        report.setRoomRevenue(totalRoomRevenue);
        report.setFnbRevenue(totalFnbRevenue);
        report.setTotalRevenue(totalRevenue);
    }

    private void calculateOccupancyMetrics(ReportData report, List<Room> rooms) {
        int totalRooms = rooms.size();
        if (totalRooms == 0) {
            report.setOccupancyRate(0); report.setReservationRate(0); return;
        }
        long occupiedCount = rooms.stream().filter(r -> "OCCUPIED".equals(String.valueOf(r.getStatus()))).count();
        long reservedCount = rooms.stream().filter(r -> "RESERVED".equals(String.valueOf(r.getStatus()))).count();

        report.setOccupancyRate(((double) occupiedCount / totalRooms) * 100);
        report.setReservationRate(((double) (occupiedCount + reservedCount) / totalRooms) * 100);
    }

    // --- 상세 데이터 생성 (기간 자동 적용) ---
    private List<Map<String, Object>> generatePeriodDetails(String start, int days, String type, ReportData calculatedData, List<ClientReservation> reservations) {
        List<Map<String, Object>> details = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        
        try {
            if (start != null) cal.setTime(sdf.parse(start));
        } catch (Exception e) {}

        // 계산된 일수(days)만큼 반복
        for (int i = 0; i < days; i++) {
            Map<String, Object> detail = new HashMap<>();
            String currentDateStr = sdf.format(cal.getTime());
            
            double dailyRoomRev = 0;
            for (ClientReservation res : reservations) {
                if (res.getCheckInDate() != null && res.getCheckInDate().equals(currentDateStr)) {
                    String status = res.getStatus();
                    if (status != null && !status.equals("CANCELLED") && !status.equals("CANCELED")) {
                        try {
                            double priceStr = res.getTotalPrice();
                        } catch (Exception e) {}
                    }
                }
            }

            double dailyFnbRev = calculatedData.getFnbRevenue() / days; // 단순 N빵

            String label = (type != null && type.equals("Daily")) ? currentDateStr : "Day " + (i + 1);
            
            detail.put("period", label);
            detail.put("roomRevenue", (int)dailyRoomRev);
            detail.put("fnbRevenue", (int)dailyFnbRev);
            detail.put("occupancy", String.format("%.1f", calculatedData.getOccupancyRate())); 
            
            details.add(detail);
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        return details;
    }

    private String generateExceptionReport() {
        return "▶ 요금 정책:\n" +
               "  - 객실 기본료: Standard 기준 50,000원 ~\n" +
               "  - 식음료: 치킨(2.0만), 피자(2.5만), 맥주(0.5만)\n" +
               "▶ 특이사항:\n" +
               "  - 식음료 주문 데이터는 전체 기간 합산으로 제공됩니다.\n" +
               "  - 객실 매출은 예약 확정(CONFIRMED) 및 체크인 건 기준입니다.";
    }
}