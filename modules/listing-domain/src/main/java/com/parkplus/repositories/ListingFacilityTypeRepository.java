package com.parkplus.repositories;

import com.parkplus.entities.ListingFacilityType;
import com.parkplus.entities.ListingFacilityTypeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListingFacilityTypeRepository extends JpaRepository<ListingFacilityType, ListingFacilityTypeId> {

    List<ListingFacilityType> findById_ListingId(UUID listingId);
}
