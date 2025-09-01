package com.parkplus.repositories;

import com.parkplus.entities.ListingAmenity;
import com.parkplus.entities.ListingAmenityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListingAmenityRepository extends JpaRepository<ListingAmenity, ListingAmenityId> {
    List<ListingAmenity> findById_ListingId(UUID listingId);
}
