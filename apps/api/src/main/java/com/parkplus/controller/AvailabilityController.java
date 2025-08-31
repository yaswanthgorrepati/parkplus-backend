package com.parkplus.controller;

import com.parkplus.dto.AvailabilityDtos;
import com.parkplus.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings/{listingId}/availability")
public class AvailabilityController {

    @Autowired
    private ListingService listingService;


    /**
     *date range (inclusive start, exclusive end).
     */
    @PostMapping
    public Map<String, Object> upsert(Authentication auth,
                                      @PathVariable(name = "listingId") UUID listingId,
                                      @Valid @RequestBody AvailabilityDtos.AvailabilityUpsertReq body) {
        UUID hostId = UUID.fromString(auth.getName());
        listingService.upsertAvailabilityRange(listingId, hostId, body);
        return Map.of("ok", true);
    }

    //seed default data
    @PostMapping("/seed")
    public Map<String, Object> seed(Authentication auth,
                                    @PathVariable(name = "listingId") UUID listingId,
                                    @Valid @RequestBody AvailabilityDtos.AvailabilitySeedReq body) {
        UUID hostId = UUID.fromString(auth.getName());
        int inserted = listingService.seedAvailabilityDefault(listingId, hostId, body.startDate(), body.endDate());
        return Map.of("inserted", inserted);
    }
}
