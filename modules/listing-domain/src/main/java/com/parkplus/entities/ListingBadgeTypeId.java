package com.parkplus.entities;

import jakarta.persistence.Embeddable;

import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ListingBadgeTypeId {
    private UUID listingId;

    private String badgeType;

    public ListingBadgeTypeId() {
    }

    public ListingBadgeTypeId(UUID listingId, String badgeType) {
        this.listingId = listingId;
        this.badgeType = badgeType;
    }

    public UUID getListingId() {
        return listingId;
    }

    public void setListingId(UUID listingId) {
        this.listingId = listingId;
    }

    public String getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(String badgeType) {
        this.badgeType = badgeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListingBadgeTypeId that = (ListingBadgeTypeId) o;
        return Objects.equals(listingId, that.listingId) && Objects.equals(badgeType, that.badgeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listingId, badgeType);
    }
}
