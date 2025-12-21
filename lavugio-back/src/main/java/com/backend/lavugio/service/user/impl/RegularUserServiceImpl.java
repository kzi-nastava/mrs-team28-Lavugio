package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.service.user.RegularUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
public class RegularUserServiceImpl implements RegularUserService {

    @Autowired
    private RegularUserRepository regularUserRepository;

    @Override
    @Transactional
    public RegularUser createRegularUser(RegularUser user) {
        // Provera da li email već postoji
        if (regularUserRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        // Postavi početne vrednosti
        user.setBlocked(false);
        user.setBlockReason(null);

        return regularUserRepository.save(user);
    }

    @Override
    @Transactional
    public RegularUser updateRegularUser(Long id, RegularUser user) {
        RegularUser existing = regularUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RegularUser not found with id: " + id));

        // Provera da li se email menja
        if (!existing.getEmail().equals(user.getEmail()) &&
                regularUserRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }

        existing.setName(user.getName());
        existing.setLastName(user.getLastName());
        existing.setEmail(user.getEmail());
        existing.setPassword(user.getPassword());
        existing.setProfilePhotoPath(user.getProfilePhotoPath());

        return regularUserRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteRegularUser(Long id) {
        RegularUser user = regularUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RegularUser not found with id: " + id));

        regularUserRepository.delete(user);
    }

    @Override
    public RegularUser getRegularUserById(Long id) {
        return regularUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RegularUser not found with id: " + id));
    }

    @Override
    public RegularUser getRegularUserByEmail(String email) {
        return regularUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("RegularUser not found with email: " + email));
    }

    @Override
    public List<RegularUser> getAllRegularUsers() {
        return regularUserRepository.findAll();
    }

    @Override
    @Transactional
    public RegularUser blockUser(Long userId, String reason) {
        RegularUser user = getRegularUserById(userId);
        user.setBlocked(true);
        user.setBlockReason(reason);
        return regularUserRepository.save(user);
    }

    @Override
    @Transactional
    public RegularUser unblockUser(Long userId) {
        RegularUser user = getRegularUserById(userId);
        user.setBlocked(false);
        user.setBlockReason(null);
        return regularUserRepository.save(user);
    }

    @Override
    public List<RegularUser> getBlockedUsers() {
        return regularUserRepository.findAll().stream()
                .filter(user -> user.isBlocked())
                .toList();
    }

    @Override
    public List<RegularUser> getActiveUsers() {
        return regularUserRepository.findAll().stream()
                .filter(user -> !user.isBlocked())
                .toList();
    }

    @Override
    public Set<Ride> getUserRides(Long userId) {
        RegularUser user = getRegularUserById(userId);
        return user.getRides();
    }

    @Override
    @Transactional
    public void addRideToUser(Long userId, Ride ride) {
        RegularUser user = getRegularUserById(userId);
        user.getRides().add(ride);
        regularUserRepository.save(user);
    }

    @Override
    @Transactional
    public void removeRideFromUser(Long userId, Ride ride) {
        RegularUser user = getRegularUserById(userId);
        user.getRides().remove(ride);
        regularUserRepository.save(user);
    }
}