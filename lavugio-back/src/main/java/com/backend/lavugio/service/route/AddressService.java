package com.backend.lavugio.service.route;

import com.backend.lavugio.model.route.Address;

import java.util.List;
import java.util.Optional;

public interface AddressService {
    Address createAddress(Address address);
    Address updateAddress(Long id, Address address);
    void deleteAddress(Long id);
    Optional<Address> getAddressById(Long id);
    List<Address> getAllAddresses();
    List<Address> searchAddresses(String city, String country, Integer zipCode);
    Optional<Address> findOrCreateAddress(Address address);
    List<Address> findAddressesWithinBounds(double minLat, double maxLat, double minLng, double maxLng);
    boolean addressExists(Address address);
}