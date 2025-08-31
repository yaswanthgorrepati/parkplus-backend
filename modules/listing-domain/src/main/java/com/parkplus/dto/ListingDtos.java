package com.parkplus.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public class ListingDtos {

    public record ListingCreateRequest(
            @NotBlank String title,
            String description,
            @NotBlank String type,              // STORAGE | PARKING | LUGGAGE
            @NotBlank String city,
            String state,
            String addressLine,
            String postalCode,
            Double lat,
            Double lng,
            @Min(1) int capacitySpaces,
            @Min(0) int basePricePerDayPaise,
            List<@NotBlank String> facilities,
            List<@NotBlank String> imageUrls
    ) {
    }

    public record ListingCreateRes(String id) {
    }

    public record ListingSearchResponse(
            String id, String title, String city, String state, int basePricePerDayPaise, String currency
    ) {
    }

    public record ListingDetailsResponse(
            String id, String title, String description, String city, String state,
            int basePricePerDayPaise, String currency, int capacitySpaces,
            List<String> facilities, List<String> images
    ) {
    }

    public record ListingSearchQuery(
            String type, String city, LocalDate sDate, LocalDate eDate, Integer qty, Integer page, Integer size
    ) {
    }
}
