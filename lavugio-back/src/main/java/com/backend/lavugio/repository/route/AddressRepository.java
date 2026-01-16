package com.backend.lavugio.repository.route;

import com.backend.lavugio.model.route.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<Address> findByStreetNameAndStreetNumberAndCity(String streetName, int streetNumber, String city);

    List<Address> findByCity(String city);

    List<Address> findByCityAndCountry(String city, String country);

    List<Address> findByCountry(String country);

    @Query("SELECT a FROM Address a WHERE " +
            "(:city IS NULL OR a.city = :city) AND " +
            "(:country IS NULL OR a.country = :country) AND " +
            "(:zipCode IS NULL OR a.zipCode = :zipCode)")
    List<Address> searchAddresses(String city, String country, Integer zipCode);

    boolean existsByStreetNameAndStreetNumberAndCity(String streetName, int streetNumber, String city);

    @Query("SELECT a FROM Address a WHERE " +
            "a.latitude BETWEEN :minLat AND :maxLat AND " +
            "a.longitude BETWEEN :minLng AND :maxLng")
    List<Address> findWithinBounds(double minLat, double maxLat, double minLng, double maxLng);

    long countByCity(String city);
}