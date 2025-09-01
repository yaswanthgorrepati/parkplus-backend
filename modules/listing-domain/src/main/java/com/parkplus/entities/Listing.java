package com.parkplus.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "listings")
public class Listing {

    @Id
    @Column(length = 36, columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(name = "host_id", length = 36, columnDefinition = "CHAR(36)", nullable = false)
    private UUID hostId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type = Type.PARKING;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String accessibility;

    private String addressLine;

    private String city;

    private String state;

    private String postalCode;

    private Double lat;

    private Double lng;

    private int capacitySpaces = 1;

    private int basePricePerDayPaise;

    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PUBLISHED;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    public enum Type {STORAGE, PARKING, LUGGAGE}

    public enum Visibility {DRAFT, PUBLISHED, UNPUBLISHED}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getHostId() {
        return hostId;
    }

    public void setHostId(UUID hostId) {
        this.hostId = hostId;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(String accessibility) {
        this.accessibility = accessibility;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public int getCapacitySpaces() {
        return capacitySpaces;
    }

    public void setCapacitySpaces(int capacitySpaces) {
        this.capacitySpaces = capacitySpaces;
    }

    public int getBasePricePerDayPaise() {
        return basePricePerDayPaise;
    }

    public void setBasePricePerDayPaise(int basePricePerDayPaise) {
        this.basePricePerDayPaise = basePricePerDayPaise;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
