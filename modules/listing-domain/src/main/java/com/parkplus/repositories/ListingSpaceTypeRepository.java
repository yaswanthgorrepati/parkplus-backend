package com.parkplus.repositories;

import com.parkplus.entities.ListingSpaceType;
import com.parkplus.entities.ListingSpaceTypeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListingSpaceTypeRepository extends JpaRepository<ListingSpaceType, ListingSpaceTypeId> {
    List<ListingSpaceType> findById_ListingId(UUID listingId);
}
