package com.parkplus.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
        name = "availability_calendar",
        uniqueConstraints = @UniqueConstraint(columnNames = {"listing_id", "date_"})
)
public class AvailabilityCalendar {

    @Id
    @Column(length = 36, columnDefinition = "CHAR(36)")
    private UUID id;

    @Column(name = "listing_id", length = 36, columnDefinition = "CHAR(36)", nullable = false)
    private UUID listingId;

    @Column(name = "date_", nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private int availableSpaces;

    private Integer pricePerDayPaise;

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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getAvailableSpaces() {
        return availableSpaces;
    }

    public void setAvailableSpaces(int availableSpaces) {
        this.availableSpaces = availableSpaces;
    }

    public Integer getPricePerDayPaise() {
        return pricePerDayPaise;
    }

    public void setPricePerDayPaise(Integer pricePerDayPaise) {
        this.pricePerDayPaise = pricePerDayPaise;
    }
}
