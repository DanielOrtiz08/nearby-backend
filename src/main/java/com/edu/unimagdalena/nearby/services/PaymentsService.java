package com.edu.unimagdalena.nearby.services;

import com.edu.unimagdalena.nearby.entities.Payment;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PaymentsService {
    Map<String, Object> createPaymentIntent(Map<String, Object> payload);
    Payment confirmPayment(Map<String, Object> payload);
    List<Payment> paymentHistory(UUID userId);
    Map<String, Object> requestRefund(Map<String, Object> payload);
}
