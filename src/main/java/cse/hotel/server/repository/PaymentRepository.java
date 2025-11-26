package cse.hotel.server.repository;

import cse.hotel.common.model.Payment;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {
    private static final String FILE_PATH = "data/payments.ser";
    private static final PaymentRepository instance = new PaymentRepository();
    private List<Payment> paymentList;

    private PaymentRepository() {
        File file = new File(FILE_PATH);
        if (file.getParentFile() != null) file.getParentFile().mkdirs();
        this.paymentList = load();
    }

    public static PaymentRepository getInstance() { return instance; }

    public void addPayment(Payment payment) {
        paymentList.add(payment);
        save();
    }

    public List<Payment> findAll() { return new ArrayList<>(paymentList); }

    private void save() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(paymentList);
            System.out.println("💾 결제 내역 저장 완료");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @SuppressWarnings("unchecked")
    private List<Payment> load() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Payment>) ois.readObject();
        } catch (Exception e) { return new ArrayList<>(); }
    }
}