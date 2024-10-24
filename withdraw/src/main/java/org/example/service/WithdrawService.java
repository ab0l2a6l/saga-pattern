package org.example.service;

public interface PaymentService {

    String refundPayment(Long orderId, String amount);
}
