package com.parkplus.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ListingAmenityId implements Serializable {

    private UUID listingId;

    private String amenity;

    public ListingAmenityId() {
    }

    public ListingAmenityId(UUID listingId, String amenity) {
        this.listingId = listingId;
        this.amenity = amenity;
    }

    public UUID getListingId() {
        return listingId;
    }

    public void setListingId(UUID listingId) {
        this.listingId = listingId;
    }

    public String getAmenity() {
        return amenity;
    }

    public void setAmenity(String amenity) {
        this.amenity = amenity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListingAmenityId that)) return false;
        return Objects.equals(listingId, that.listingId) && Objects.equals(amenity, that.amenity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listingId, amenity);
    }
}
