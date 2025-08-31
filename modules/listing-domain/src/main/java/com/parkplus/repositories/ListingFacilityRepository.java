package com.parkplus.repositories;

import com.parkplus.entities.ListingFacility;
import com.parkplus.entities.ListingFacilityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListingFacilityRepository extends JpaRepository<ListingFacility, ListingFacilityId> {
    List<ListingFacility> findById_ListingId(UUID listingId);
}
