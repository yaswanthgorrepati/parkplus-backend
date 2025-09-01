package com.parkplus.repositories;

import com.parkplus.entities.ListingVehicleType;
import com.parkplus.entities.ListingVehicleTypeId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ListingVehicleTypeRepository extends JpaRepository<ListingVehicleType, ListingVehicleTypeId> {
    List<ListingVehicleType> findById_ListingId(UUID listingId);
}
