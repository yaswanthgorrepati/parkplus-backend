package com.parkplus.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    @Column(length = 36, columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(length = 36, columnDefinition = "CHAR(36)", nullable = false)
    private UUID bookingId;

    @Column(nullable = false)
    private String provider = "RAZORPAY";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.CREATED;

    private String orderId;    // Razorpay order_...

    private String paymentId;  // Razorpay pay_...

    private String signature;  // Razorpay signature

    @Column(nullable = false)
    private int amountPaise; // paise in INR context

    @Column(nullable = false)
    private String currency = "INR";

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public enum Status {CREATED, CAPTURED, FAILED, REFUNDED}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID v) {
        this.bookingId = v;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String v) {
        this.provider = v;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status v) {
        this.status = v;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String v) {
        this.orderId = v;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String v) {
        this.paymentId = v;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String v) {
        this.signature = v;
    }

    public int getAmountPaise() {
        return amountPaise;
    }

    public void setAmountPaise(int amountPaise) {
        this.amountPaise = amountPaise;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String v) {
        this.currency = v;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant v) {
        this.createdAt = v;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant v) {
        this.updatedAt = v;
    }
}
