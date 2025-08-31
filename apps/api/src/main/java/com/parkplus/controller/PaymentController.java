package com.parkplus.controller;

import com.parkplus.service.PaymentService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    @Autowired
    private PaymentService payments;

    // Client calls this to get an orderId to open Razorpay Checkout
    @PostMapping("/create-order")
    public Map<String, Object> createOrder(Authentication auth,
                                           @RequestBody CreateOrderReq body) {
        UUID customerId = UUID.fromString(auth.getName());
        var res = payments.createOrder(UUID.fromString(body.bookingId()), customerId);
        return Map.of(
                "orderId", res.orderId(),
                "keyId", res.keyId(),          // frontend needs this for Checkout
                "amount", res.amount(),        // paise
                "currency", res.currency()
        );
    }

    // After Checkout success, frontend posts ids to verify
    @PostMapping("/verify")
    public Map<String, Object> verify(Authentication auth,
                                      @RequestBody VerifyReq body) {
        UUID customerId = UUID.fromString(auth.getName());
        var res = payments.verifyAndCapture(body.orderId(), body.paymentId(), body.signature(), customerId);
        return Map.of("ok", res.ok(), "bookingStatus", res.bookingStatus());
    }

    public record CreateOrderReq(@NotBlank String bookingId) {
    }

    public record VerifyReq(@NotBlank String orderId, @NotBlank String paymentId, @NotBlank String signature) {
    }
}
