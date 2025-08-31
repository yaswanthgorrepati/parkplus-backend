package com.parkplus.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "listing_facilities")
public class ListingFacility {

    @EmbeddedId
    private ListingFacilityId id;

    public ListingFacility() {
    }

    public ListingFacility(ListingFacilityId id) {
        this.id = id;
    }

    public ListingFacilityId getId() {
        return id;
    }

    public void setId(ListingFacilityId v) {
        this.id = v;
    }
}
