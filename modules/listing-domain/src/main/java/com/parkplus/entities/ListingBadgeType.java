package com.parkplus.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "listing_badge_type")
public class ListingBadgeType {

    @EmbeddedId
    private ListingBadgeTypeId id;

    public ListingBadgeType() {
    }

    public ListingBadgeType(ListingBadgeTypeId id) {
        this.id = id;
    }

    public ListingBadgeTypeId getId() {
        return id;
    }

    public void setId(ListingBadgeTypeId id) {
        this.id = id;
    }
}
