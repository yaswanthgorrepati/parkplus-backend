package com.parkplus.repository;

import com.parkplus.entities.BookingVehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingVehicleRepository extends JpaRepository<BookingVehicle, UUID> {
}
