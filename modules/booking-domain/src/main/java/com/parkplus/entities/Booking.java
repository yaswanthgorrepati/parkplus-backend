package com.parkplus.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @Column(length = 36, columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(length = 36, columnDefinition = "CHAR(36)", nullable = false)
    private UUID listingId;

    @Column(length = 36, columnDefinition = "CHAR(36)", nullable = false)
    private UUID customerId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "booking_id")  // FK lives in booking_vehicles
    private List<BookingVehicle> bookingVehicles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate; // exclusive (checkout day)

    @Column(nullable = false)
    private int spacesQty = 1;

    @Column(nullable = false)
    private int subtotalPasie;

    @Column(nullable = false)
    private int feesPasie;

    @Column(nullable = false)
    private int totalPaise;

    @Column(nullable = false)
    private String currency = "INR";

    @Column(unique = true)
    private String referenceCode; // e.g., BK-2025-XXXX

    public enum Status {
        AWAITING_PAYMENT, PAID, ACTIVE, COMPLETED, CANCELLED, REFUNDED
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getListingId() {
        return listingId;
    }

    public void setListingId(UUID listingId) {
        this.listingId = listingId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public List<BookingVehicle> getBookingVehicles() {
        return bookingVehicles;
    }

    public void setBookingVehicles(List<BookingVehicle> bookingVehicles) {
        this.bookingVehicles = bookingVehicles;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getSpacesQty() {
        return spacesQty;
    }

    public void setSpacesQty(int spacesQty) {
        this.spacesQty = spacesQty;
    }

    public int getSubtotalPasie() {
        return subtotalPasie;
    }

    public void setSubtotalPasie(int subtotalPasie) {
        this.subtotalPasie = subtotalPasie;
    }

    public int getFeesPasie() {
        return feesPasie;
    }

    public void setFeesPasie(int feesPasie) {
        this.feesPasie = feesPasie;
    }

    public int getTotalPaise() {
        return totalPaise;
    }

    public void setTotalPaise(int totalPaise) {
        this.totalPaise = totalPaise;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public void setReferenceCode(String referenceCode) {
        this.referenceCode = referenceCode;
    }
}
