package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.dto.user.UpdateUserDTO;
import com.backend.lavugio.dto.user.UserDTO;
import com.backend.lavugio.dto.user.UserRegistrationDTO;
import com.backend.lavugio.exception.EmailAlreadyExistsException;
import com.backend.lavugio.exception.UserNotFoundException;
import com.backend.lavugio.model.user.RegularUser;
import com.backend.lavugio.model.ride.Ride;
import com.backend.lavugio.repository.user.RegularUserRepository;
import com.backend.lavugio.service.user.RegularUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegularUserServiceImpl implements RegularUserService {

    private static final Logger logger = LoggerFactory.getLogger(RegularUserServiceImpl.class);

    @Autowired
    private RegularUserRepository regularUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Override
    public UserDTO createRegularUser(UserRegistrationDTO request) {
        logger.info("Registering new user with email: {}", request.getEmail());
        
        // Check if email already exists
        if (regularUserRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email already exists: {}", request.getEmail());
            throw new EmailAlreadyExistsException("Email already exists: " + request.getEmail());
        }

        // Create RegularUser
        RegularUser user = new RegularUser();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Hash password
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setProfilePhotoPath(request.getProfilePhotoPath());
        user.setAddress(request.getAddress());
        user.setBlocked(false);
        user.setBlockReason(null);
        user.setEmailVerified(false); // Not verified until email confirmation
        user.setCanOrder(true); // New users can order by default

        RegularUser savedUser = regularUserRepository.save(user);
        logger.info("User registered successfully with id: {}", savedUser.getId());
        return mapToDTO(savedUser);
    }

    @Override
    public UserDTO getRegularUserDTOById(Long id) {
        RegularUser user = regularUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToDTO(user);
    }

    @Override
    public UserDTO getUserDTOByEmail(String email) {
        RegularUser user = regularUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return mapToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsersDTO() {
        List<RegularUser> users = regularUserRepository.findAll();
        return users.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO updateRegularUser(Long id, UpdateUserDTO request) {
        RegularUser user = regularUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update fields
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfilePhotoPath() != null) {
            user.setProfilePhotoPath(request.getProfilePhotoPath());
        }

        RegularUser updatedUser = regularUserRepository.save(user);
        return mapToDTO(updatedUser);
    }


    @Override
    public UserDTO getRegularUserProfile(Long id) {
        RegularUser user = regularUserRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToProfileDTO(user);
    }

    @Override
    public UserDTO getUserProfileById(Long id) {
        // Same as getRegularUserProfile
        return getRegularUserProfile(id);
    }

    @Override
    public boolean emailExists(String email) {
        return regularUserRepository.existsByEmail(email);
    }

    // Helper method to map entity to DTO
    private UserDTO mapToDTO(RegularUser user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setAddress(user.getAddress());
        dto.setProfilePhotoPath(user.getProfilePhotoPath());
        dto.setBlocked(user.isBlocked());
        dto.setBlockReason(user.getBlockReason());
        dto.setEmailVerified(user.isEmailVerified());
        return dto;
    }

    private UserDTO mapToProfileDTO(RegularUser user) {
        UserDTO profile = new UserDTO();
        profile.setId(user.getId());
        profile.setName(user.getName());
        profile.setLastName(user.getLastName());
        profile.setEmail(user.getEmail());
        profile.setPhoneNumber(user.getPhoneNumber());
        profile.setAddress(user.getAddress());
        profile.setProfilePhotoPath(user.getProfilePhotoPath());
        profile.setBlocked(user.isBlocked());
        profile.setBlockReason(user.getBlockReason());
        profile.setEmailVerified(user.isEmailVerified());

        // TODO: Add ride history and other profile info
        //profile.setTotalRides(0);
        //profile.setTotalSpent(0.0);

        return profile;
    }

    @Override
    public void enableUserOrdering(Long userId) {
        throw new RuntimeException("Not implemented");
    }

    @Override
    public void disableUserOrdering(Long userId) {
        throw new RuntimeException("Not implemented");
    }
}