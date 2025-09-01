package com.parkplus.entities;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ListingSpaceTypeId {

    private UUID listingId;

    private String spaceType;

    public ListingSpaceTypeId() {
    }

    public ListingSpaceTypeId(UUID listingId, String spaceType) {
        this.listingId = listingId;
        this.spaceType = spaceType;
    }

    public UUID getListingId() {
        return listingId;
    }

    public void setListingId(UUID listingId) {
        this.listingId = listingId;
    }

    public String getSpaceType() {
        return spaceType;
    }

    public void setSpaceType(String spaceType) {
        this.spaceType = spaceType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListingSpaceTypeId that = (ListingSpaceTypeId) o;
        return Objects.equals(listingId, that.listingId) && Objects.equals(spaceType, that.spaceType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listingId, spaceType);
    }
}
