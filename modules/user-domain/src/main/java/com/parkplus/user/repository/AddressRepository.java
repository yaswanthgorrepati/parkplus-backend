package com.parkplus.user.repository;

import com.parkplus.user.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AddressRepository extends JpaRepository<Address, UUID> {
  List<Address> findByUserId(UUID userId);
}