package com.parkplus.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ListingFacilityId implements Serializable {

    private UUID listingId;

    private String facility;

    public ListingFacilityId() {
    }

    public ListingFacilityId(UUID listingId, String facility) {
        this.listingId = listingId;
        this.facility = facility;
    }

    public UUID getListingId() {
        return listingId;
    }

    public void setListingId(UUID v) {
        this.listingId = v;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String v) {
        this.facility = v;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ListingFacilityId that)) return false;
        return Objects.equals(listingId, that.listingId) && Objects.equals(facility, that.facility);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listingId, facility);
    }
}
