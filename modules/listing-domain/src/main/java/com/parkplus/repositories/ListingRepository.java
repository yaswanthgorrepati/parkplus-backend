package com.parkplus.repositories;

import com.parkplus.entities.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ListingRepository extends JpaRepository<Listing, UUID> {
    Page<Listing> findByTypeAndVisibilityAndCityIgnoreCaseContaining(
            Listing.Type type, Listing.Visibility visibility, String city, Pageable pageable);
}
