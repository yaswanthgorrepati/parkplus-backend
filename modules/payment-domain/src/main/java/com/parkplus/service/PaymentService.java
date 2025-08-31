package com.parkplus.service;


import com.parkplus.common.error.ApiException;
import com.parkplus.common.error.ErrorCodes;
import com.parkplus.entities.AvailabilityCalendar;
import com.parkplus.entities.Booking;
import com.parkplus.entities.Listing;
import com.parkplus.entities.Payment;
import com.parkplus.repositories.AvailabilityCalendarRepository;
import com.parkplus.repositories.ListingRepository;
import com.parkplus.repository.BookingRepository;
import com.parkplus.repository.PaymentRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private AvailabilityCalendarRepository availabilityCalendarRepository;
    @Value("${razorpay.keyId:}")
    private String keyId;
    @Value("${razorpay.keySecret:}")
    private String keySecret;

    /**
     * Create Razorpay order
     */
    @Transactional
    public CreateOrderResponse createOrder(UUID bookingId, UUID customerId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new ApiException(ErrorCodes.NOT_FOUND, "Booking not found", 404));

        if (!booking.getCustomerId().equals(customerId)) {
            throw new ApiException(ErrorCodes.AUTH_FAILED, "You can create order only for your booking", 403);
        }
        if (booking.getStatus() != Booking.Status.AWAITING_PAYMENT) {
            throw new ApiException(ErrorCodes.CONFLICT, "Booking not awaiting payment", 409);
        }

        int amountPaise = booking.getTotalPaise();
        String currency = booking.getCurrency();

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setBookingId(booking.getId());
        payment.setAmountPaise(amountPaise);
        payment.setCurrency(currency);
        payment.setStatus(Payment.Status.CREATED);

        if (keyId != null && !keyId.isBlank() && keySecret != null && !keySecret.isBlank()) {
            try {
                RazorpayClient client = new RazorpayClient(keyId, keySecret);
                JSONObject orderRequest = new JSONObject()
                        .put("amount", amountPaise)
                        .put("currency", currency)
                        .put("receipt", "BK-" + booking.getId())
                        .put("payment_capture", 1);
                Order order = client.orders.create(orderRequest);
                payment.setOrderId(order.get("id"));
            } catch (Exception e) {
                throw new ApiException(ErrorCodes.INTERNAL_ERROR, "Razorpay order create failed", 500);
            }
        } else {
            // test – no keys – create a fake order id
            payment.setOrderId("order_dev_" + UUID.randomUUID().toString().substring(0, 8));
        }

        paymentRepository.save(payment);
        return new CreateOrderResponse(payment.getOrderId(), keyId, amountPaise, currency);
    }

    @Transactional
    public VerifyResponse verifyAndCapture(String orderId, String paymentId, String signature, UUID customerId) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() ->
                new ApiException(ErrorCodes.NOT_FOUND, "Payment order not found", 404));
        Booking booking = bookingRepository.findById(payment.getBookingId()).orElseThrow(() ->
                new ApiException(ErrorCodes.NOT_FOUND, "Booking not found", 404));

        if (!booking.getCustomerId().equals(customerId)) {
            throw new ApiException(ErrorCodes.AUTH_FAILED, "You can verify only your booking", 403);
        }
        if (booking.getStatus() != Booking.Status.AWAITING_PAYMENT) {
            throw new ApiException(ErrorCodes.CONFLICT, "Booking not awaiting payment", 409);
        }

        boolean isPaymentSignatureVerified = false;
        try {
            isPaymentSignatureVerified = Utils.verifyPaymentSignature(
                    new org.json.JSONObject().put("razorpay_order_id", orderId)
                            .put("razorpay_payment_id", paymentId)
                            .put("razorpay_signature", signature),
                    keySecret
            );
        } catch (Exception e) {
            System.out.println(e);
        }

        if (!isPaymentSignatureVerified) {
            payment.setStatus(Payment.Status.FAILED);
            payment.setPaymentId(paymentId);
            payment.setSignature(signature);
            paymentRepository.save(payment);
            throw new ApiException(ErrorCodes.AUTH_FAILED, "Payment signature invalid", 401);
        }

        // Mark payment and booking
        payment.setStatus(Payment.Status.CAPTURED);
        payment.setPaymentId(paymentId);
        payment.setSignature(signature);
        paymentRepository.save(payment);

        booking.setStatus(Booking.Status.PAID);
        bookingRepository.save(booking);

        // decrement per day
        decrementAvailability(booking);

        return new VerifyResponse(true, booking.getStatus().name());
    }

    /**
     * Decrement availability for each booked date (exclusive enddate)
     */
    private void decrementAvailability(Booking booking) {
        Listing listing = listingRepository.findById(booking.getListingId()).orElseThrow();
        LocalDate startDate = booking.getStartDate();
        while (startDate.isBefore(booking.getEndDate())) {
            Optional<AvailabilityCalendar> opt = availabilityCalendarRepository.findByListingIdAndDate(listing.getId(), startDate);

            LocalDate temp = startDate;
            AvailabilityCalendar availabilityCalendar = opt.orElseThrow(() ->
                    new ApiException(ErrorCodes.CONFLICT, "Availability not set for " + temp, 409));

            int remaining = availabilityCalendar.getAvailableSpaces() - booking.getSpacesQty();
            if (remaining < 0) {
                throw new ApiException(ErrorCodes.CONFLICT, "No availability left on " + startDate, 409);
            }

            availabilityCalendar.setAvailableSpaces(remaining);
            availabilityCalendarRepository.save(availabilityCalendar);
            startDate = startDate.plusDays(1);
        }
    }

    // DTOs returned by service
    public record CreateOrderResponse(String orderId, String keyId, int amount, String currency) {
    }

    public record VerifyResponse(boolean ok, String bookingStatus) {
    }
}
