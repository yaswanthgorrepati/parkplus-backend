package com.parkplus.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public class ListingDtos {

    public record ListingCreateRequest(
            @NotBlank String title,
            String description,
            @NotBlank String type,// STORAGE | PARKING | LUGGAGE
            @NotBlank String accessibility,
            @NotBlank String city,
            String state,
            String addressLine,
            String postalCode,
            Double lat,
            Double lng,
            @Min(1) int capacitySpaces,
            @Min(0) int basePricePerDayPaise,
            List<@NotBlank String> amenities,
            List<@NotBlank String> imageUrls,
            List<@NotBlank String> vehicleTypes,
            List<@NotBlank String> facilityTypes,
            List<@NotBlank String> spaceTypes,
            List<@NotBlank String> badgeTypes
    ) {
    }

    public record ListingCreateRes(String id) {
    }

//    public record ListingSearchResponse(
//            String id, String title, String city, String state, int basePricePerDayPaise, String currency
//    ) {
//    }

    public record ListingSearchResponse(
            String id, String title, int capacitySpaces, String imgUrl, String city, String state,
            int basePricePerDayPaise, String currency, List<String> badgeTypes
    ) {
    }

    public record ListingDetailsResponse(
            String id, String title, String description, String accessibility, String city, String state,
            int basePricePerDayPaise, String currency, int capacitySpaces,
            List<String> amenities, List<String> vehicleTypes, List<String> facilityTypes,
            List<String> spaceTypes, List<String> badgeTypes, List<String> images, Host host
    ) {
    }

    public record Host(String name, String imageUrl, HostDetails details) {
    }

    public record HostDetails(boolean emailVerified, boolean phoneNumberVerified, boolean govtIdVerified) {
    }

    public record ListingSearchQuery(
            String type, String city, LocalDate sDate, LocalDate eDate, Integer qty, Integer page, Integer size
    ) {
    }
}
