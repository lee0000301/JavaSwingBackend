package cse.hotel.server.service;

import cse.hotel.common.exception.DuplicateIdException;
import cse.hotel.common.exception.DataNotFoundException;
import cse.hotel.server.repository.CustomerRepository;
import cse.hotel.common.model.Customer;
import java.util.List;

public class CustomerService {

    // 1. Singleton 인스턴스
    private static final CustomerService instance = new CustomerService();
    
    // Repository 인스턴스를 가져옵니다.
    private final CustomerRepository repository = CustomerRepository.getInstance();

    // 2. private 생성자로 외부 생성 차단
    private CustomerService() {
        // 서버 시작 시 Repository 초기화 (데이터 로드)
    }

    // 3. 인스턴스 접근 메서드
    public static CustomerService getInstance() {
        return instance;
    }

    // --- Helper Method: 전화번호 중복 확인 ---
    private boolean isPhoneNumberDuplicated(String phoneNumber, String currentId) {
        return repository.findAllCustomers().stream()
                // 현재 수정하려는 고객(currentId) 자신을 제외하고 전화번호 중복을 검사
                .filter(c -> !c.getCustomerId().equals(currentId))
                .anyMatch(c -> c.getPhoneNumber().equals(phoneNumber));
    }
    
    // --- CRUD 메서드 (ClientHandler 호출용) ---

    // R: 전체 고객 목록 조회
    public List<Customer> getAllCustomers() {
        return repository.findAllCustomers();
    }

   // C: 고객 등록 (ADD_CUSTOMER 명령)
public Customer addCustomer(Customer newCustomer) throws DuplicateIdException, IllegalArgumentException {
    
    // 1. 🌟 ID를 먼저 생성하여 객체에 설정합니다. (NPE 방지) 🌟
    // 클라이언트가 null로 보낸 ID를 즉시 채워줍니다.
    String newId = "CUST-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    newCustomer.setCustomerId(newId);
    
    // 2. 필수 필드 검사
    if (newCustomer.getName().isEmpty() || newCustomer.getPhoneNumber().isEmpty()) {
        // ID는 이미 설정되었으므로 이름/전화번호만 체크
        throw new IllegalArgumentException("고객 이름과 전화번호는 필수 입력 사항입니다.");
    }
    
    // 3. 전화번호 중복 검사 (전화번호를 고유 키처럼 사용)
    // 이 시점에서 newCustomer는 이미 유효한 ID를 가지고 있습니다.
//    if (repository.findCustomerByPhone(newCustomer.getPhoneNumber()) != null) {
//        throw new DuplicateIdException("오류: 이미 등록된 전화번호입니다. (" + newCustomer.getPhoneNumber() + ")");
//    }
    
    // 4. Repository Add 및 저장
    repository.addCustomer(newCustomer);
    
    return newCustomer; // 새로 생성된 ID가 포함된 객체를 반환합니다.
}

    // U: 고객 정보 수정 (UPDATE_CUSTOMER 명령)
    public void updateCustomer(Customer updatedCustomer) throws DataNotFoundException, DuplicateIdException, IllegalArgumentException {
        // 1. 필수 필드 검사
        if (updatedCustomer.getName().isEmpty() || updatedCustomer.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("고객 이름과 전화번호는 필수 입력 사항입니다.");
        }

        // 2. 수정 대상 객실 ID 존재 확인
        if (repository.findCustomerById(updatedCustomer.getCustomerId()) == null) {
            throw new DataNotFoundException("오류: 수정하려는 고객 ID를 찾을 수 없습니다.");
        }
        
        // 3. 전화번호 중복 검사 (수정하려는 ID 자신을 제외하고 중복 검사)
        if (isPhoneNumberDuplicated(updatedCustomer.getPhoneNumber(), updatedCustomer.getCustomerId())) {
             throw new DuplicateIdException("오류: 입력된 전화번호는 이미 다른 고객에게 등록되어 있습니다.");
        }
        
        // 4. Repository에 수정 요청
        repository.updateCustomer(updatedCustomer);
    }

    // D: 고객 삭제 (DELETE_CUSTOMER 명령)
    public void deleteCustomer(String customerId) throws DataNotFoundException {
        Customer removed = repository.deleteCustomer(customerId);
        
        if (removed == null) {
            throw new DataNotFoundException("오류: 삭제하려는 고객 ID를 찾을 수 없습니다.");
        }
    }
    
    // R: ID로 고객 1명 조회
    public Customer getCustomerById(String customerId) {
        return repository.findCustomerById(customerId);
    }
    
    // R: 전화번호로 고객 1명 조회 (예약 모듈 연동용)
    public Customer getCustomerByPhone(String phoneNumber) {
        return repository.findCustomerByPhone(phoneNumber);
    }
}