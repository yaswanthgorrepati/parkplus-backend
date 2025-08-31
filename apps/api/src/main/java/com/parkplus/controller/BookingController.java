package com.parkplus.controller;

import com.parkplus.dto.BookingDtos;
import com.parkplus.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    // POST /api/v1/bookings/preview  (public)
    @PostMapping("/preview")
    public BookingDtos.BookingPreviewResposne preview(@RequestBody BookingDtos.BookingPreviewRequest bookingPreviewRequest) {
        return bookingService.preview(
                UUID.fromString(bookingPreviewRequest.listingId()),
                bookingPreviewRequest.startDate(),
                bookingPreviewRequest.endDate(),
                bookingPreviewRequest.spacesQty()
        );
    }

    // POST /api/v1/bookings  (customer creates — needs auth)
    @PostMapping
    public BookingDtos.BookingCreateResposne create(Authentication auth, @Valid @RequestBody BookingDtos.BookingCreateRequest bookingCreateRequest) {
        UUID customerId = UUID.fromString(auth.getName()); // subject from JWT
        return bookingService.create(
                customerId,
                UUID.fromString(bookingCreateRequest.listingId()),
                bookingCreateRequest.startDate(),
                bookingCreateRequest.endDate(),
                bookingCreateRequest.spacesQty()
        );
    }

    // POST /api/v1/bookings/{id}/decide  (host accepts/rejects — needs auth)
//    @PostMapping("/{id}/decide")
//    public Map<String, Object> decide(@PathVariable(name = "id") UUID id,
//                                      Authentication auth,
//                                      @Valid @RequestBody BookingDtos.BookingDecisionReq body) {
//        boolean accept = "ACCEPT".equalsIgnoreCase(body.decision());
//        var updated = svc.decide(id, UUID.fromString(auth.getName()), accept);
//        return Map.of("id", updated.getId(), "status", updated.getStatus());
//    }
}
