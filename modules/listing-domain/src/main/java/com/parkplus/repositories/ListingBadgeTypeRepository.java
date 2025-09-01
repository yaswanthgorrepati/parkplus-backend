package com.parkplus.repositories;

import com.parkplus.entities.ListingBadgeType;
import com.parkplus.entities.ListingBadgeTypeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListingBadgeTypeRepository extends JpaRepository<ListingBadgeType, ListingBadgeTypeId> {
    List<ListingBadgeType> findById_ListingId(UUID listingId);
}
