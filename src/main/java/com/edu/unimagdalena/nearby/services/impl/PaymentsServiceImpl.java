package com.edu.unimagdalena.nearby.services.impl;

import com.edu.unimagdalena.nearby.entities.Payment;
import com.edu.unimagdalena.nearby.entities.CuentaUsuario;
import com.edu.unimagdalena.nearby.repositories.PaymentRepository;
import com.edu.unimagdalena.nearby.repositories.CuentaUsuarioRepository;
import com.edu.unimagdalena.nearby.services.PaymentsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class PaymentsServiceImpl implements PaymentsService {

    private final PaymentRepository paymentRepo;
    private final CuentaUsuarioRepository cuentaRepo;

    public PaymentsServiceImpl(PaymentRepository paymentRepo,
                               CuentaUsuarioRepository cuentaRepo) {
        this.paymentRepo = paymentRepo;
        this.cuentaRepo = cuentaRepo;
    }

    @Override
    public Map<String, Object> createPaymentIntent(Map<String, Object> payload) {
        if (payload == null || payload.get("usuarioId") == null || payload.get("monto") == null) {
            throw new RuntimeException("usuarioId y monto son obligatorios");
        }
        UUID usuarioId = UUID.fromString(payload.get("usuarioId").toString());
        CuentaUsuario user = cuentaRepo.findById(usuarioId).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        BigDecimal monto;
        try {
            monto = new BigDecimal(payload.get("monto").toString());
        } catch (Exception e) {
            throw new RuntimeException("monto inválido");
        }
        String moneda = payload.getOrDefault("moneda", "COP").toString();

        String intentId = UUID.randomUUID().toString();
        Payment p = new Payment();
        p.setUsuario(user);
        p.setMonto(monto);
        p.setMoneda(moneda);
        p.setStatus("PENDING");
        p.setIntentId(intentId);
        p.setCreatedAt(LocalDateTime.now());
        p.setProvider(payload.getOrDefault("provider", "mock").toString());
        p.setMetadata(payload.getOrDefault("metadata", "").toString());

        paymentRepo.save(p);

        Map<String, Object> res = new HashMap<>();
        res.put("intentId", intentId);
        res.put("clientSecret", "secret_" + intentId); // simulated
        res.put("payment", p);
        return res;
    }

    @Override
    public Payment confirmPayment(Map<String, Object> payload) {
        if (payload == null || payload.get("intentId") == null) {
            throw new RuntimeException("intentId es obligatorio");
        }
        String intentId = payload.get("intentId").toString();
        Payment p = paymentRepo.findByIntentId(intentId).orElseThrow(() -> new RuntimeException("Payment intent no encontrado"));
        if (!"PENDING".equalsIgnoreCase(p.getStatus())) {
            throw new RuntimeException("Payment no en estado PENDING");
        }
        p.setStatus("CONFIRMED");
        p.setConfirmedAt(LocalDateTime.now());
        paymentRepo.save(p);
        return p;
    }

    @Override
    public List<Payment> paymentHistory(UUID userId) {
        return paymentRepo.findByUsuario_IdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Map<String, Object> requestRefund(Map<String, Object> payload) {
        if (payload == null || payload.get("intentId") == null) {
            throw new RuntimeException("intentId es obligatorio");
        }
        String intentId = payload.get("intentId").toString();
        Payment p = paymentRepo.findByIntentId(intentId).orElseThrow(() -> new RuntimeException("Payment no encontrado"));
        if (!"CONFIRMED".equalsIgnoreCase(p.getStatus())) {
            throw new RuntimeException("Solo pagos confirmados pueden solicitar reembolso");
        }
        p.setStatus("REFUND_REQUESTED");
        paymentRepo.save(p);

        Map<String, Object> res = new HashMap<>();
        res.put("message", "Solicitud de reembolso registrada");
        res.put("paymentId", p.getId());
        return res;
    }
}
