package com.parkplus.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "listing_amenities")
public class ListingAmenity {

    @EmbeddedId
    private ListingAmenityId id;

    public ListingAmenity() {
    }

    public ListingAmenity(ListingAmenityId id) {
        this.id = id;
    }

    public ListingAmenityId getId() {
        return id;
    }

    public void setId(ListingAmenityId id) {
        this.id = id;
    }
}
