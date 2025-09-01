package com.parkplus.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

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
            @Min(1) int spacesQty,
            List<BookingVehicleDto> vehicles
    ) {
    }

    public record BookingVehicleDto(
            @NotBlank String vehicleType,
            String registrationNumber
    ) {
    }

    public record BookingCreateResposne(String id, String referenceCode, String status) {
    }

    public record BookingDecisionRequest(@NotBlank String decision, String reason) {
    }

    public record BookingResponse(UUID id, String referenceCode, String status, UUID listingId,
                                  LocalDate startDate, LocalDate endDate, int spacesQty, int subtotalCents,
                                  int feesCents, int totalCents, String currency, List<BookingVehicleDto> vehicles) {
    }


    public record BookingHistoryDto(UUID bookingId, String referenceCode, String status, LocalDate startDate,
                                    LocalDate endDate, int spacesQty, int totalCents, String currency,
                                    ListingInfo listing,
                                    PaymentInfo payment, List<BookingVehicleDto> vehicles
    ) {
    }

    public record ListingInfo(UUID id, String title, String city,String state, String type, String imgUrl) {
    }

    public record PaymentInfo(UUID id, String status, String orderId, String paymentId) {
    }

}
