package com.parkplus.controller;

import com.parkplus.dto.BookingDtos;
import com.parkplus.service.BookingService;
import com.parkplus.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {
    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

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
                bookingCreateRequest.spacesQty(),
                bookingCreateRequest.vehicles()
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

    @GetMapping("/{id}")
    public BookingDtos.BookingResponse getById(Authentication auth, @PathVariable(name = "id") UUID id) {
        UUID customerId = UUID.fromString(auth.getName()); // subject from JWT
        return bookingService.getById(id, customerId);
    }

    // BookingController.java
    @GetMapping("/history")
    public Page<BookingDtos.BookingHistoryDto> getHistory(Authentication auth,
                                                          @RequestParam(name = "page", defaultValue = "0") int page,
                                                          @RequestParam(name = "size", defaultValue = "10") int size) {
        UUID customerId = UUID.fromString(auth.getName());
        return paymentService.getBookingHistory(customerId, page, size);
    }

}
