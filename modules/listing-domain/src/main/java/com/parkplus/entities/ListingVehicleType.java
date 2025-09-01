package com.parkplus.entities;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "listing_vehicle_type")
public class ListingVehicleType {

    @EmbeddedId
    private  ListingVehicleTypeId id;

    public ListingVehicleType() {
    }

    public ListingVehicleType(ListingVehicleTypeId id) {
        this.id = id;
    }

    public ListingVehicleTypeId getId() {
        return id;
    }

    public void setId(ListingVehicleTypeId id) {
        this.id = id;
    }
}
