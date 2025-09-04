package com.parkplus.user.controller;

import com.parkplus.user.entities.Address;
import com.parkplus.user.repository.AddressRepository;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {
    @Autowired
    private AddressRepository addressRepository;

    record CreateAddressReq(
            String label, @NotBlank String line1, String line2, @NotBlank String city,
            String state, String countryCode, String postalCode,
            java.math.BigDecimal lat, java.math.BigDecimal lng, Boolean isDefault
    ) {
    }

    @PostMapping
    public Map<String, Object> create(Authentication auth, @RequestBody CreateAddressReq createAddressReq) {
        UUID uid = UUID.fromString(auth.getName());
        List<Address> addressList = addressRepository.findByUserId(uid);

        Address address = null;
        if(CollectionUtils.isEmpty(addressList)){
            address = new Address();
            address.setId(UUID.randomUUID());
        }else {
            address = addressList.get(0);
        }

        address.setUserId(uid);
        address.setLabel(createAddressReq.label());
        address.setLine1(createAddressReq.line1());
        address.setLine2(createAddressReq.line2());
        address.setCity(createAddressReq.city());
        address.setState(createAddressReq.state());
        address.setCountryCode(createAddressReq.countryCode());
        address.setPostalCode(createAddressReq.postalCode());
        address.setLat(createAddressReq.lat());
        address.setLng(createAddressReq.lng());
        address.setDefault(Boolean.TRUE.equals(createAddressReq.isDefault()));
        addressRepository.save(address);
        return Map.of("id", address.getId());
    }
}
