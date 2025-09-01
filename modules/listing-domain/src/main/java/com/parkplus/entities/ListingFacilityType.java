package com.parkplus.entities;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "listing_facility_type")
public class ListingFacilityType {

    @EmbeddedId
    private ListingFacilityTypeId id;


    public ListingFacilityType() {
    }

    public ListingFacilityType(ListingFacilityTypeId id) {
        this.id = id;
    }

    public ListingFacilityTypeId getId() {
        return id;
    }

    public void setId(ListingFacilityTypeId id) {
        this.id = id;
    }
}
