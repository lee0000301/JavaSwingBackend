package cse.hotel.server.repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID; // 고객 ID 자동 생성을 위해 추가
import cse.hotel.common.model.Customer;

public class CustomerRepository {

    // 파일 경로 설정
    private static final String FILE_NAME = "data/customer_data.ser";
    private static final File DATA_FILE = new File(FILE_NAME);
    
    // 싱글톤 인스턴스
    private static final CustomerRepository instance = new CustomerRepository();

    // 메모리상의 고객 목록 (DB 역할)
    private final List<Customer> customerDatabase;

    // --- Static 초기화 블록 (파일 및 폴더 생성 강제) ---
    static {
        // data 폴더가 없으면 생성
        File parentDir = DATA_FILE.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
            System.out.println("✅ 'data' 폴더 생성 완료.");
        }
    }
    // --------------------------------------------------

    // 3. private 생성자: 데이터 로드 및 초기 데이터 설정
    private CustomerRepository() {
        this.customerDatabase = loadData();
        
        // 데이터 파일이 비어있는 경우, 테스트용 기본 고객을 추가합니다.
        if (customerDatabase.isEmpty()) {
            System.out.println("고객 데이터 파일이 비어 있어 기본 데이터를 추가합니다.");
            addInitialCustomers();
            saveData(); // 기본 데이터 저장
        }
    }

    // 기본 테스트 고객 데이터 추가
    private void addInitialCustomers() {
        customerDatabase.add(new Customer(generateId(), "김철수", "010-1234-5678"));
        customerDatabase.add(new Customer(generateId(), "이영희", "010-9876-5432"));
    }

    // 고객 ID 생성 메서드 (편의상 UUID 사용)
    private String generateId() {
        // "CUST-" + UUID의 앞 8자리만 사용
        return "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    // 4. 인스턴스 접근 메서드
    public static CustomerRepository getInstance() {
        return instance;
    }

    // =========================================================================
    //                            파일 입출력 로직 (I/O)
    // =========================================================================

    /**
     * 파일에서 고객 목록을 불러옵니다. (Load)
     * @return 파일에서 읽어온 Customer List (파일이 없거나 비어있으면 빈 List 반환)
     */
    @SuppressWarnings("unchecked")
    private List<Customer> loadData() {
        List<Customer> loadedList = new ArrayList<>();
        
        // 파일이 존재하고 크기가 0보다 커야 데이터를 읽어옴
        if (DATA_FILE.exists() && DATA_FILE.length() > 0) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
                loadedList = (List<Customer>) ois.readObject();
                System.out.println("✅ 고객 데이터 로드 성공: " + loadedList.size() + "명");
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("❌ 고객 데이터 파일 로드 실패: " + e.getMessage());
            }
        } else {
            System.out.println("고객 데이터 파일이 없어 새로 생성 준비 완료.");
        }
        return loadedList;
    }

    /**
     * 메모리의 고객 목록을 파일에 저장(덮어쓰기)합니다. (Save)
     */
    public synchronized void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(customerDatabase);
            System.out.println("💾 고객 데이터 저장 완료.");
        } catch (IOException e) {
            System.err.println("❌ 고객 데이터 저장 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =========================================================================
    //                            CRUD 메서드 (DB 접근)
    // =========================================================================

    /** C: 새 고객 등록 */
    public Customer addCustomer(Customer customer) {
        // 아이디나 전화번호 중복 체크 로직은 Service 계층에서 수행하는 것이 일반적
        customerDatabase.add(customer);
        saveData();
        return customer;
    }

    /** R: 모든 고객 목록 조회 */
    public List<Customer> findAllCustomers() {
        // 복사본을 반환하여 외부에서 원본 리스트를 직접 수정하는 것을 방지
        return new ArrayList<>(customerDatabase); 
    }

    /** R: ID로 고객 1명 조회 */
    public Customer findCustomerById(String customerId) {
        return customerDatabase.stream()
                .filter(c -> c.getCustomerId().equals(customerId))
                .findFirst()
                .orElse(null);
    }
    
    /** R: 전화번호로 고객 1명 조회 (예약 모듈에서 유용) */
    public Customer findCustomerByPhone(String phoneNumber) {
        return customerDatabase.stream()
                .filter(c -> c.getPhoneNumber().equals(phoneNumber))
                .findFirst()
                .orElse(null);
    }

    /** U: 고객 정보 수정 */
    public Customer updateCustomer(Customer updatedCustomer) {
        for (int i = 0; i < customerDatabase.size(); i++) {
            if (customerDatabase.get(i).getCustomerId().equals(updatedCustomer.getCustomerId())) {
                customerDatabase.set(i, updatedCustomer);
                saveData();
                return updatedCustomer;
            }
        }
        return null; // 수정 실패 (ID를 찾지 못함)
    }

    /** D: 고객 삭제 */
    public Customer deleteCustomer(String customerId) {
        Customer customerToRemove = findCustomerById(customerId);
        if (customerToRemove != null) {
            customerDatabase.remove(customerToRemove);
            saveData();
            return customerToRemove;
        }
        return null; // 삭제 실패 (ID를 찾지 못함)
    }
}