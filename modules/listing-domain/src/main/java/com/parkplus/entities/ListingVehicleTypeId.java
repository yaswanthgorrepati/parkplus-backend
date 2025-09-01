package com.parkplus.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ListingVehicleTypeId implements Serializable {
    private UUID listingId;

    private String vehicleType;

    public ListingVehicleTypeId() {
    }

    public ListingVehicleTypeId(String vehicleType, UUID listingId) {
        this.vehicleType = vehicleType;
        this.listingId = listingId;
    }

    public UUID getListingId() {
        return listingId;
    }

    public void setListingId(UUID listingId) {
        this.listingId = listingId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListingVehicleTypeId that = (ListingVehicleTypeId) o;
        return Objects.equals(listingId, that.listingId) && Objects.equals(vehicleType, that.vehicleType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listingId, vehicleType);
    }
}
