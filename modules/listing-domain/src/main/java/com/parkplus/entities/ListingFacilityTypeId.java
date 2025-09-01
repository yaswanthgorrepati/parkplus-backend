package com.parkplus.entities;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ListingFacilityTypeId {

    private UUID listingId;

    private String facilityType;

    public ListingFacilityTypeId() {
    }

    public ListingFacilityTypeId(String facilityType, UUID listingId) {
        this.facilityType = facilityType;
        this.listingId = listingId;
    }

    public UUID getListingId() {
        return listingId;
    }

    public void setListingId(UUID listingId) {
        this.listingId = listingId;
    }

    public String getFacilityType() {
        return facilityType;
    }

    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListingFacilityTypeId that = (ListingFacilityTypeId) o;
        return Objects.equals(listingId, that.listingId) && Objects.equals(facilityType, that.facilityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listingId, facilityType);
    }
}
