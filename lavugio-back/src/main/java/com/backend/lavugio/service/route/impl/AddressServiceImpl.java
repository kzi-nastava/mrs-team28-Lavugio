package com.backend.lavugio.service.route.impl;

import com.backend.lavugio.model.route.Address;
import com.backend.lavugio.repository.route.AddressRepository;
import com.backend.lavugio.service.route.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    @Transactional
    public Address createAddress(Address address) {
        // Provera da li adresa već postoji
        Optional<Address> existing = addressRepository
                .findByStreetNameAndStreetNumberAndCity(
                        address.getStreetName(),
                        address.getStreetNumber(),
                        address.getCity()
                );

        return existing.orElseGet(() -> addressRepository.save(address));
    }

    @Override
    @Transactional
    public Address updateAddress(Long id, Address address) {
        Address existing = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        existing.setStreetName(address.getStreetName());
        existing.setStreetNumber(address.getStreetNumber());
        existing.setCity(address.getCity());
        existing.setCountry(address.getCountry());
        existing.setZipCode(address.getZipCode());
        existing.setLongitude(address.getLongitude());
        existing.setLatitude(address.getLatitude());

        return addressRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found with id: " + id));

        // TODO: Proveriti da li se adresa koristi negde pre brisanja
        // (u ride destinacijama ili favorite rutama)
        addressRepository.delete(address);
    }

    @Override
    public Optional<Address> getAddressById(Long id) {
        return addressRepository.findById(id);
    }

    @Override
    public List<Address> getAllAddresses() {
        return addressRepository.findAll();
    }

    @Override
    public List<Address> searchAddresses(String city, String country, Integer zipCode) {
        return addressRepository.searchAddresses(city, country, zipCode);
    }

    @Override
    @Transactional
    public Optional<Address> findOrCreateAddress(Address address) {
        Optional<Address> existing = addressRepository
                .findByStreetNameAndStreetNumberAndCity(
                        address.getStreetName(),
                        address.getStreetNumber(),
                        address.getCity()
                );

        if (existing.isPresent()) {
            return existing;
        }

        return Optional.of(addressRepository.save(address));
    }

    @Override
    public List<Address> findAddressesWithinBounds(double minLat, double maxLat, double minLng, double maxLng) {
        return addressRepository.findWithinBounds(minLat, maxLat, minLng, maxLng);
    }

    @Override
    public boolean addressExists(Address address) {
        return addressRepository.existsByStreetNameAndStreetNumberAndCity(
                address.getStreetName(),
                address.getStreetNumber(),
                address.getCity()
        );
    }
}