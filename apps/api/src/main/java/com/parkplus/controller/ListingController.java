package com.parkplus.controller;

import com.parkplus.common.web.PageResponse;
import com.parkplus.dto.ListingDtos;
import com.parkplus.service.ListingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings")
public class ListingController {

    @Autowired
    private ListingService listingService;

    //  /api/v1/listings  (requires auth)
    @PostMapping("")
    public Map<String, Object> create(Authentication auth, @Valid @RequestBody ListingDtos.ListingCreateRequest body) {
        // In JWT, we put subject = userId
        UUID hostId = UUID.fromString(auth.getName());
        UUID id = listingService.create(hostId, body);
        return Map.of("id", id);
    }

    //  /api/v1/listings/{id}  (public)
    @GetMapping("/{id}")
    public ListingDtos.ListingDetailsResponse details(@PathVariable("id") UUID id) {
        return listingService.details(id);
    }


    //  /api/v1/listings/search?type=&city=&sDate=&eDate=&qty=&page=&size=
    @GetMapping("/search")
    public PageResponse<ListingDtos.ListingSearchResponse> search(
            @RequestParam(required = false, name = "type") String type,
            @RequestParam(required = false, name = "city") String city,
            @RequestParam(required = false, name = "sDate") String sDate,
            @RequestParam(required = false, name = "eDate") String eDate,
            @RequestParam(required = false, name = "qty") Integer qty,
            @RequestParam(required = false, name = "page") Integer page,
            @RequestParam(required = false, name = "size") Integer size,
            @RequestParam(required = false, name = "vehicleTypes") String vehicleTypes,
            @RequestParam(required = false, name = "facilityTypes") String facilityTypes,
            @RequestParam(required = false, name = "spaceTypes") String spaceTypes) {
        ListingDtos.ListingSearchQuery listingSearchQuery = new ListingDtos.ListingSearchQuery(type, city, sDate != null ? LocalDate.parse(sDate) : null,
                eDate != null ? LocalDate.parse(eDate) : null, qty, page, size,
                vehicleTypes != null ? Arrays.asList(vehicleTypes.split(",")) : null,
                facilityTypes != null ? Arrays.asList(facilityTypes.split(",")) : null,
                spaceTypes != null ? Arrays.asList(spaceTypes.split(",")) : null);

        Page<ListingDtos.ListingSearchResponse> p = listingService.search(listingSearchQuery);

        return PageResponse.of(p);
    }
}
