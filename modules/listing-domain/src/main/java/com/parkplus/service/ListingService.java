package com.parkplus.service;

import com.parkplus.common.error.ApiException;
import com.parkplus.common.error.ErrorCodes;
import com.parkplus.dto.AvailabilityDtos;
import com.parkplus.dto.ListingDtos;
import com.parkplus.entities.*;
import com.parkplus.repositories.AvailabilityCalendarRepository;
import com.parkplus.repositories.ListingFacilityRepository;
import com.parkplus.repositories.ListingImageRepository;
import com.parkplus.repositories.ListingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private ListingImageRepository listingImageRepository;
    @Autowired
    private ListingFacilityRepository listingFacilityRepository;
    @Autowired
    private AvailabilityCalendarRepository availabilityCalendarRepository;


    @Transactional
    public UUID create(UUID hostId, ListingDtos.ListingCreateRequest request) {
        Listing listing = new Listing();
        listing.setId(UUID.randomUUID());
        listing.setHostId(hostId);
        listing.setType(Listing.Type.valueOf(request.type()));
        listing.setTitle(request.title());
        listing.setDescription(request.description());
        listing.setCity(request.city());
        listing.setState(request.state());
        listing.setAddressLine(request.addressLine());
        listing.setPostalCode(request.postalCode());
        listing.setLat(request.lat());
        listing.setLng(request.lng());
        listing.setCapacitySpaces(request.capacitySpaces());
        listing.setBasePricePerDayPaise(request.basePricePerDayPaise());
        listingRepository.save(listing);

        if (request.facilities() != null) {
            for (String facility : request.facilities()) {
                listingFacilityRepository.save(new ListingFacility(new ListingFacilityId(listing.getId(), facility)));
            }
        }
        if (request.imageUrls() != null) {
            int pos = 0;
            for (String url : request.imageUrls()) {
                ListingImage listingImage = new ListingImage();
                listingImage.setId(UUID.randomUUID());
                listingImage.setListingId(listing.getId());
                listingImage.setUrl(url);
                listingImage.setPosition(pos++);
                listingImageRepository.save(listingImage);
            }
        }
        return listing.getId();
    }

    public ListingDtos.ListingDetailsResponse details(UUID id) {
        Listing listing = listingRepository.findById(id).orElseThrow(() ->
                new ApiException(ErrorCodes.NOT_FOUND, "Listing not found", 404));

        List<String> img = listingImageRepository.findByListingIdOrderByPositionAsc(id).stream().map(ListingImage::getUrl).toList();

        List<String> fs = listingFacilityRepository.findById_ListingId(id).stream().map(x -> x.getId().getFacility()).toList();

        return new ListingDtos.ListingDetailsResponse(

                listing.getId().toString(), listing.getTitle(), listing.getDescription(), listing.getCity(), listing.getState(),
                listing.getBasePricePerDayPaise(), listing.getCurrency(), listing.getCapacitySpaces(), fs, img
        );
    }

    public Page<ListingDtos.ListingSearchResponse> search(ListingDtos.ListingSearchQuery listingSearchQuery) {
        Listing.Type type = Listing.Type.valueOf(Optional.ofNullable(listingSearchQuery.type()).orElse("PARKING"));
        String city = Optional.ofNullable(listingSearchQuery.city()).orElse("");
        int page = Optional.ofNullable(listingSearchQuery.page()).orElse(0);
        int size = Optional.ofNullable(listingSearchQuery.size()).orElse(12);

        Page<Listing> pageResponse = listingRepository.findByTypeAndVisibilityAndCityIgnoreCaseContaining(
                type, Listing.Visibility.PUBLISHED, city, PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        // Optional availability filter (only if sDate/eDate/qty provided)
        LocalDate startDate = listingSearchQuery.sDate();
        LocalDate endDate = listingSearchQuery.eDate();
        Integer qty = listingSearchQuery.qty();
        List<Listing> filtered = new ArrayList<>(pageResponse.getContent());
        if (startDate != null && endDate != null && qty != null && !endDate.isBefore(startDate)) {
            long totalDaysRequested = startDate.until(endDate).getDays();
            filtered = filtered.stream().filter(l -> {
                long totalDaysAvailable = availabilityCalendarRepository.countDaysAvailable(l.getId(), startDate, endDate, qty);
                return totalDaysAvailable == totalDaysRequested; // available for every day
            }).collect(Collectors.toList());
        }

        List<ListingDtos.ListingSearchResponse> listingSearchResponses = filtered.stream()
                .map(listing -> new ListingDtos.ListingSearchResponse(
                        listing.getId().toString(), listing.getTitle(), listing.getCity(), listing.getState(),
                        listing.getBasePricePerDayPaise(), listing.getCurrency()))
                .toList();

        return new PageImpl<>(listingSearchResponses, pageResponse.getPageable(), pageResponse.getTotalElements());
    }

    @Transactional
    public void upsertAvailabilityRange(UUID listingId, UUID hostId, AvailabilityDtos.AvailabilityUpsertReq availabilityUpsertReq) {
        if (availabilityUpsertReq.endDate().isBefore(availabilityUpsertReq.startDate()) || availabilityUpsertReq.endDate().equals(availabilityUpsertReq.startDate())) {
            throw new com.parkplus.common.error.ApiException(
                    com.parkplus.common.error.ErrorCodes.VALIDATION_ERROR,
                    "endDate must be after startDate", 400);
        }

        Listing listing = listingRepository.findById(listingId).orElseThrow(() ->
                new com.parkplus.common.error.ApiException(
                        com.parkplus.common.error.ErrorCodes.NOT_FOUND, "Listing not found", 404));

        if (!listing.getHostId().equals(hostId)) {
            throw new com.parkplus.common.error.ApiException(
                    com.parkplus.common.error.ErrorCodes.AUTH_FAILED, "Only the host can modify availability", 403);
        }

        int days = (int) ChronoUnit.DAYS.between(availabilityUpsertReq.startDate(), availabilityUpsertReq.endDate());
        for (int i = 0; i < days; i++) {
            LocalDate d = availabilityUpsertReq.startDate().plusDays(i);
            var existing = availabilityCalendarRepository.findByListingIdAndDate(listingId, d);
            AvailabilityCalendar row = existing.orElseGet(() -> {
                AvailabilityCalendar availabilityCalendar = new AvailabilityCalendar();
                availabilityCalendar.setId(UUID.randomUUID());
                availabilityCalendar.setListingId(listingId);
                availabilityCalendar.setDate(d);
                return availabilityCalendar;
            });
            row.setAvailableSpaces(availabilityUpsertReq.availableSpaces());

            //price override
            if (availabilityUpsertReq.pricePerDayPaise() != null) {
                row.setPricePerDayPaise(availabilityUpsertReq.pricePerDayPaise());
            } else if (existing.isEmpty()) {
                row.setPricePerDayPaise(listing.getBasePricePerDayPaise());
            }
            availabilityCalendarRepository.save(row);
        }
    }

    @Transactional
    public int seedAvailabilityDefault(UUID listingId, UUID hostId, LocalDate start, LocalDate end) {
        Listing listing = listingRepository.findById(listingId).orElseThrow(() ->
                new com.parkplus.common.error.ApiException(
                        com.parkplus.common.error.ErrorCodes.NOT_FOUND, "Listing not found", 404));

        if (!listing.getHostId().equals(hostId)) {
            throw new com.parkplus.common.error.ApiException(
                    com.parkplus.common.error.ErrorCodes.AUTH_FAILED, "Only the host can modify availability", 403);
        }

        int days = (int) ChronoUnit.DAYS.between(start, end);
        int saved = 0;
        for (int i = 0; i < days; i++) {
            LocalDate d = start.plusDays(i);
            var existing = availabilityCalendarRepository.findByListingIdAndDate(listingId, d);
            if (existing.isPresent()) continue;
            AvailabilityCalendar availabilityCalendar = new AvailabilityCalendar();
            availabilityCalendar.setId(UUID.randomUUID());
            availabilityCalendar.setListingId(listingId);
            availabilityCalendar.setDate(d);
            availabilityCalendar.setAvailableSpaces(listing.getCapacitySpaces());
            availabilityCalendar.setPricePerDayPaise(listing.getBasePricePerDayPaise());
            availabilityCalendarRepository.save(availabilityCalendar);
            saved++;
        }
        return saved;
    }
}
