package com.parkplus.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "listing_space_type")
public class ListingSpaceType {

    @EmbeddedId
    private ListingSpaceTypeId id;

    public ListingSpaceType() {
    }

    public ListingSpaceType(ListingSpaceTypeId id) {
        this.id = id;
    }

    public ListingSpaceTypeId getId() {
        return id;
    }

    public void setId(ListingSpaceTypeId id) {
        this.id = id;
    }
}
