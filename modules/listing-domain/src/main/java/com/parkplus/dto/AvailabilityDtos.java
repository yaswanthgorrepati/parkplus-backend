package com.parkplus.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class AvailabilityDtos {

    public record AvailabilityUpsertReq(
            @NotNull LocalDate startDate,        // inclusive
            @NotNull LocalDate endDate,          // exclusive
            @Min(0) int availableSpaces,         // per day
            Integer pricePerDayPaise)        // optional override, null = keep current or use base price
    { }

    public record AvailabilitySeedReq(
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate)
    { }
}
