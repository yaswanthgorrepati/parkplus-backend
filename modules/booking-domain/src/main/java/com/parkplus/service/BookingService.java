package com.parkplus.service;

import com.parkplus.common.error.ApiException;
import com.parkplus.common.error.ErrorCodes;
import com.parkplus.dto.BookingDtos;
import com.parkplus.entities.Booking;
import com.parkplus.entities.BookingVehicle;
import com.parkplus.entities.Listing;
import com.parkplus.repositories.AvailabilityCalendarRepository;
import com.parkplus.repositories.ListingRepository;
import com.parkplus.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private AvailabilityCalendarRepository availabilityCalendarRepository;


    public BookingDtos.BookingPreviewResposne preview(UUID listingId, LocalDate startDate, LocalDate endDate, int availableSpaces) {
        if (endDate.isBefore(startDate) || endDate.equals(startDate)) {
            throw new ApiException(ErrorCodes.VALIDATION_ERROR, "endDate must be after startDate", 400);
        }
        Listing listing = listingRepository.findById(listingId).orElseThrow(() ->
                new ApiException(ErrorCodes.NOT_FOUND, "Listing not found", 404));

        int nights = (int) ChronoUnit.DAYS.between(startDate, endDate);
        // Availability: must be >= listing for every day in range
        long ok = availabilityCalendarRepository.countDaysAvailable(listingId, startDate, endDate, availableSpaces);
        boolean available = (ok == nights);

        int base = listing.getBasePricePerDayPaise();
        int subtotal = nights * base * availableSpaces;
        int fees = Math.max((int) Math.round(subtotal * 0.05), 1000); // 5% or min ₹10
        int total = subtotal + fees;

        return new BookingDtos.BookingPreviewResposne(nights, base, subtotal, fees, total, listing.getCurrency(), available);
    }

    @Transactional
    public BookingDtos.BookingCreateResposne create(UUID customerId, UUID listingId, LocalDate start, LocalDate end,
                                                    int qty, List<BookingDtos.BookingVehicleDto> vehicles) {
        BookingDtos.BookingPreviewResposne bookingPreviewResposne = preview(listingId, start, end, qty);
        if (!bookingPreviewResposne.available())
            throw new ApiException(ErrorCodes.CONFLICT, "Not available for the selected dates/qty", 409);

        Booking booking = new Booking();
        booking.setId(UUID.randomUUID());
        booking.setListingId(listingId);
        booking.setCustomerId(customerId);
        booking.setStartDate(start);
        booking.setEndDate(end);
        booking.setSpacesQty(qty);
        booking.setSubtotalPasie(bookingPreviewResposne.subtotalPaise());
        booking.setFeesPasie(bookingPreviewResposne.feesPaise());
        booking.setTotalPaise(bookingPreviewResposne.totalPaise());
        booking.setCurrency(bookingPreviewResposne.currency());
        booking.setStatus(Booking.Status.AWAITING_PAYMENT);
        booking.setReferenceCode(generateRef());
        booking.getBookingVehicles().clear();


        if (vehicles != null) {
            for (BookingDtos.BookingVehicleDto bookingVehicleRequest : vehicles) {
                BookingVehicle bookingVehicle = new BookingVehicle();
                bookingVehicle.setId(UUID.randomUUID());
                bookingVehicle.setVehicleType(bookingVehicleRequest.vehicleType());
                bookingVehicle.setRegistrationNumber(bookingVehicleRequest.registrationNumber());
                booking.getBookingVehicles().add(bookingVehicle);
            }
        }

        bookingRepository.save(booking);

        return new BookingDtos.BookingCreateResposne(booking.getId().toString(), booking.getReferenceCode(), booking.getStatus().name());
    }

//    @Transactional
//    public Booking decide(UUID bookingId, UUID actingUserId, boolean accept) {
//        Booking b = bookings.findById(bookingId).orElseThrow(() ->
//                new ApiException(ErrorCodes.NOT_FOUND, "Booking not found", 404));
//
//        // Verify the acting user is the HOST who owns the listing
//        Listing l = listings.findById(b.getListingId()).orElseThrow(() ->
//                new ApiException(ErrorCodes.NOT_FOUND, "Listing not found", 404));
//        if (!l.getHostId().equals(actingUserId)) {
//            throw new ApiException(ErrorCodes.AUTH_FAILED, "Only the host can decide", 403);
//        }
//
//        if (b.getStatus() != Booking.Status.PENDING) {
//            throw new ApiException(ErrorCodes.CONFLICT, "Invalid state transition", 409);
//        }
//
//        if (accept) {
//            b.setStatus(Booking.Status.AWAITING_PAYMENT); // host accepted → ask customer to pay
//        } else {
//            b.setStatus(Booking.Status.HOST_REJECTED);
//        }
//        return b; // JPA flushes on commit
//
//    }

    @Transactional(readOnly = true)
    public BookingDtos.BookingResponse getById(UUID bookingId, UUID customerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ApiException(ErrorCodes.NOT_FOUND, "Booking not found", 404));

        if (!booking.getCustomerId().equals(customerId)) {
            throw new ApiException(ErrorCodes.AUTH_FAILED, "You cannot view someone else’s booking", 403);
        }

        List<BookingDtos.BookingVehicleDto> bookingVehicleList = booking.getBookingVehicles().stream()
                .map(bookingVehicle -> new BookingDtos.BookingVehicleDto(bookingVehicle.getVehicleType(), bookingVehicle.getRegistrationNumber()))
                .toList();

        return new BookingDtos.BookingResponse(
                booking.getId(),
                booking.getReferenceCode(),
                booking.getStatus().name(),
                booking.getListingId(),
                booking.getStartDate(),
                booking.getEndDate(),
                booking.getSpacesQty(),
                booking.getSubtotalPasie(),
                booking.getFeesPasie(),
                booking.getTotalPaise(),
                booking.getCurrency(),
                bookingVehicleList
        );
    }

    private String generateRef() {
        return "BK-" + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
