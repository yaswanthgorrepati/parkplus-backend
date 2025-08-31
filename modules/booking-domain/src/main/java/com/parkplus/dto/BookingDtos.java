package com.parkplus.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class BookingDtos {

    public record BookingPreviewRequest(
            @NotBlank String listingId,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,   // exclusive
            @Min(1) int spacesQty) {
    }

    public record BookingPreviewResposne(
            int nights,
            int basePricePerNightPaise,
            int subtotalPaise,
            int feesPaise,
            int totalPaise,
            String currency,
            boolean available) {
    }

    public record BookingCreateRequest(
            @NotBlank String listingId,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @Min(1) int spacesQty) {
    }

    public record BookingCreateResposne(String id, String referenceCode, String status) {
    }

    public record BookingDecisionRequest(@NotBlank String decision, String reason) {
    }

}
