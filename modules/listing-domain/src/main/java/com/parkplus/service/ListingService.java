package com.parkplus.service;

import ch.qos.logback.core.util.StringUtil;
import com.parkplus.common.error.ApiException;
import com.parkplus.common.error.ErrorCodes;
import com.parkplus.dto.AvailabilityDtos;
import com.parkplus.dto.ListingDtos;
import com.parkplus.entities.*;
import com.parkplus.repositories.*;
import com.parkplus.user.entities.Profile;
import com.parkplus.user.entities.User;
import com.parkplus.user.repository.ProfileRepository;
import com.parkplus.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ListingService {

    @Autowired
    private ListingRepository listingRepository;
    @Autowired
    private ListingImageRepository listingImageRepository;
    @Autowired
    private ListingAmenityRepository listingAmenityRepository;
    @Autowired
    private AvailabilityCalendarRepository availabilityCalendarRepository;
    @Autowired
    private ListingVehicleTypeRepository listingVehicleTypeRepository;
    @Autowired
    private ListingFacilityTypeRepository listingFacilityTypeRepository;
    @Autowired
    private ListingSpaceTypeRepository listingSpaceTypeRepository;
    @Autowired
    private ListingBadgeTypeRepository listingBadgeTypeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProfileRepository profileRepository;

    @Transactional
    public UUID create(UUID hostId, ListingDtos.ListingCreateRequest request) {
        Listing listing = new Listing();
        listing.setId(UUID.randomUUID());
        listing.setHostId(hostId);
        listing.setType(Listing.Type.valueOf(request.type()));
        listing.setTitle(request.title());
        listing.setDescription(request.description());
        listing.setAccessibility(request.accessibility());
        listing.setCity(request.city());
        listing.setState(request.state());
        listing.setAddressLine(request.addressLine());
        listing.setPostalCode(request.postalCode());
        listing.setLat(request.lat());
        listing.setLng(request.lng());
        listing.setCapacitySpaces(request.capacitySpaces());
        listing.setBasePricePerDayPaise(request.basePricePerDayPaise());
        listingRepository.save(listing);

        if (request.amenities() != null) {
            for (String amenity : request.amenities()) {
                listingAmenityRepository.save(new ListingAmenity(new ListingAmenityId(listing.getId(), amenity)));
            }
        }

        if (request.vehicleTypes() != null) {
            for (String vehicleType : request.vehicleTypes()) {
                listingVehicleTypeRepository.save(new ListingVehicleType(new ListingVehicleTypeId(vehicleType, listing.getId())));
            }
        }

        if (request.facilityTypes() != null) {
            for (String facilityType : request.facilityTypes()) {
                listingFacilityTypeRepository.save(new ListingFacilityType(new ListingFacilityTypeId(facilityType, listing.getId())));
            }
        }

        if (request.spaceTypes() != null) {
            for (String spaceType : request.spaceTypes()) {
                listingSpaceTypeRepository.save(new ListingSpaceType(new ListingSpaceTypeId(listing.getId(), spaceType)));
            }
        }

        if (request.badgeTypes() != null) {
            for (String badgeType : request.badgeTypes()) {
                listingBadgeTypeRepository.save(new ListingBadgeType(new ListingBadgeTypeId(listing.getId(), badgeType)));
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

        List<String> listingImages = listingImageRepository.findByListingIdOrderByPositionAsc(id).stream().map(ListingImage::getUrl).toList();

        List<String> listingAmenities = listingAmenityRepository.findById_ListingId(id).stream().map(x -> x.getId().getAmenity()).toList();

        List<String> listingVehicleTypes = listingVehicleTypeRepository.findById_ListingId(id).stream().map(x -> x.getId().getVehicleType()).toList();

        List<String> listingFaclityType = listingFacilityTypeRepository.findById_ListingId(id).stream().map(x -> x.getId().getFacilityType()).toList();

        List<String> listingSpaceType = listingSpaceTypeRepository.findById_ListingId(id).stream().map(x -> x.getId().getSpaceType()).toList();

        List<String> listingBadgeType = listingBadgeTypeRepository.findById_ListingId(id).stream().map(x -> x.getId().getBadgeType()).toList();

        User user = userRepository.getReferenceById(listing.getHostId());
        Profile profile = profileRepository.getReferenceById(user.getId());


        ListingDtos.HostDetails hostDetails = new ListingDtos.HostDetails(StringUtil.notNullNorEmpty(user.getEmail()), StringUtil.notNullNorEmpty(user.getPhoneNumber()), StringUtil.notNullNorEmpty(profile.getGovtIdNumber()));
        ListingDtos.Host host = new ListingDtos.Host(profile.getFirstName() + " " + profile.getLastName(), profile.getAvatarUrl(), hostDetails);

        return new ListingDtos.ListingDetailsResponse(
                listing.getId().toString(), listing.getTitle(), listing.getDescription(), listing.getAccessibility(), listing.getCity(), listing.getState(),
                listing.getBasePricePerDayPaise(), listing.getCurrency(), listing.getCapacitySpaces(), listingAmenities,
                listingVehicleTypes, listingFaclityType, listingSpaceType, listingBadgeType, listingImages, host
        );
    }

    public Page<ListingDtos.ListingSearchResponse> search(ListingDtos.ListingSearchQuery listingSearchQuery) {
        Listing.Type type = Listing.Type.valueOf(Optional.ofNullable(listingSearchQuery.type()).orElse("PARKING"));
        String city = Optional.ofNullable(listingSearchQuery.city()).orElse("");
        int page = Optional.ofNullable(listingSearchQuery.page()).orElse(0);
        int size = Optional.ofNullable(listingSearchQuery.size()).orElse(4);

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

        List<String> vehicleTypes = listingSearchQuery.vehicleTypes();
        List<String> facilityTypes = listingSearchQuery.facilityTypes();
        List<String> spaceTypes = listingSearchQuery.spaceTypes();

        if (!CollectionUtils.isEmpty(vehicleTypes)) {
            Set<String> requestedTypes = vehicleTypes.stream().map(String::toLowerCase).collect(Collectors.toSet());

            filtered = filtered.stream().filter(listing -> {
                List<ListingVehicleType> listingVehicleTypes =
                        listingVehicleTypeRepository.findById_ListingId(listing.getId());
                Set<String> dbTypes = listingVehicleTypes.stream()
                        .map(lvt -> lvt.getId().getVehicleType().toLowerCase())
                        .collect(Collectors.toSet());

                return dbTypes.containsAll(requestedTypes);
            }).toList();
        }

        if (!CollectionUtils.isEmpty(facilityTypes)) {
            Set<String> requestedTypes = facilityTypes.stream().map(String::toLowerCase).collect(Collectors.toSet());

            filtered = filtered.stream().filter(listing -> {
                List<ListingFacilityType> listingFacilityTypes =
                        listingFacilityTypeRepository.findById_ListingId(listing.getId());
                Set<String> dbTypes = listingFacilityTypes.stream()
                        .map(lvt -> lvt.getId().getFacilityType().toLowerCase())
                        .collect(Collectors.toSet());

                return dbTypes.containsAll(requestedTypes);
            }).toList();
        }

        if (!CollectionUtils.isEmpty(spaceTypes)) {
            Set<String> requestedTypes = spaceTypes.stream().map(String::toLowerCase).collect(Collectors.toSet());

            filtered = filtered.stream().filter(listing -> {
                List<ListingSpaceType> listingSpaceTypes =
                        listingSpaceTypeRepository.findById_ListingId(listing.getId());
                Set<String> dbTypes = listingSpaceTypes.stream()
                        .map(lvt -> lvt.getId().getSpaceType().toLowerCase())
                        .collect(Collectors.toSet());

                return dbTypes.containsAll(requestedTypes);
            }).toList();
        }

        List<ListingDtos.ListingSearchResponse> listingSearchResponses = filtered.stream()
                .map(listing -> {

                    Optional<String> imgUrl = listingImageRepository.findByListingIdOrderByPositionAsc(listing.getId()).stream().filter(img -> img.getPosition() == 0).toList().stream().map(ListingImage::getUrl).findFirst();
                    List<String> listingBadgeType = listingBadgeTypeRepository.findById_ListingId(listing.getId()).stream().map(x -> x.getId().getBadgeType()).toList();

                    return new ListingDtos.ListingSearchResponse(
                            listing.getId().toString(), listing.getTitle(), listing.getCapacitySpaces(), imgUrl.orElse(null),
                            listing.getCity(), listing.getState(),
                            listing.getBasePricePerDayPaise(), listing.getCurrency(), listingBadgeType);
                })
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
