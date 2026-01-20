package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.dto.user.AdministratorDTO;
import com.backend.lavugio.dto.user.AdministratorRegistrationDTO;
import com.backend.lavugio.dto.user.UserProfileDTO;
import com.backend.lavugio.model.user.Administrator;
import com.backend.lavugio.repository.user.AdministratorRepository;
import com.backend.lavugio.service.user.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdministratorServiceImpl implements AdministratorService {

    @Autowired
    private AdministratorRepository administratorRepository;

    @Override
    @Transactional
    public Administrator createAdministrator(Administrator admin) {
        // Provera da li email već postoji
        if (administratorRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + admin.getEmail());
        }

        return administratorRepository.save(admin);
    }

    @Override
    @Transactional
    public Administrator updateAdministrator(Long id, Administrator admin) {
        Administrator existing = getAdministratorById(id);

        // Provera da li se email menja
        if (!existing.getEmail().equals(admin.getEmail()) &&
                administratorRepository.findByEmail(admin.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + admin.getEmail());
        }

        existing.setName(admin.getName());
        existing.setLastName(admin.getLastName());
        existing.setEmail(admin.getEmail());
        existing.setPassword(admin.getPassword());
        existing.setProfilePhotoPath(admin.getProfilePhotoPath());

        return administratorRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteAdministrator(Long id) {
        Administrator admin = getAdministratorById(id);
        administratorRepository.delete(admin);
    }

    @Override
    public Administrator getAdministratorById(Long id) {
        return administratorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrator not found with id: " + id));
    }

    @Override
    public Optional<Administrator> getAdministratorByEmail(String email) {
        return administratorRepository.findByEmail(email);
    }

    @Override
    public List<Administrator> getAllAdministrators() {
        return administratorRepository.findAll();
    }

    // USED IN CONTROLLER

    @Override
    public void register(AdministratorRegistrationDTO request) {
        // Check if email already exists
        if (administratorRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }

        // Create Administrator
        Administrator admin = new Administrator();
        admin.setEmail(request.getEmail());
        admin.setPassword(request.getPassword()); // TODO: Add password encoding
        admin.setName(request.getName());
        admin.setLastName(request.getLastName());
        admin.setPhoneNumber(request.getPhoneNumber());
        admin.setProfilePhotoPath(request.getProfilePhotoPath());

        administratorRepository.save(admin);
    }

    @Override
    public AdministratorDTO getAdministratorDTOById(Long id) {
        Administrator admin = administratorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrator not found with id: " + id));
        return mapToDTO(admin);
    }

    @Override
    public AdministratorDTO getAdministratorDTOByEmail(String email) {
        Administrator admin = administratorRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Administrator not found with email: " + email));
        return mapToDTO(admin);
    }

    @Override
    public List<AdministratorDTO> getAllAdministratorsDTO() {
        List<Administrator> admins = administratorRepository.findAll();
        return admins.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AdministratorDTO updateAdministratorDTO(Long id, UserProfileDTO request) {
        Administrator admin = administratorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrator not found with id: " + id));

        // Update fields
        if (request.getName() != null) {
            admin.setName(request.getName());
        }
        if (request.getSurname() != null) {
            admin.setLastName(request.getSurname());
        }
        if (request.getPhoneNumber() != null) {
            admin.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getProfilePhotoPath() != null) {
            admin.setProfilePhotoPath(request.getProfilePhotoPath());
        }

        Administrator updatedAdmin = administratorRepository.save(admin);
        return mapToDTO(updatedAdmin);
    }


    @Override
    public AdministratorDTO getAdministratorProfileDTO() {
        // For now, return first admin or throw error
        // In real app, you'd get from authentication context
        List<Administrator> admins = administratorRepository.findAll();
        if (admins.isEmpty()) {
            throw new RuntimeException("No administrators found");
        }
        return mapToDTO(admins.get(0));
    }

    // Helper method to map entity to DTO
    private AdministratorDTO mapToDTO(Administrator admin) {
        AdministratorDTO dto = new AdministratorDTO();
        dto.setId(admin.getId());
        dto.setName(admin.getName());
        dto.setLastName(admin.getLastName());
        dto.setEmail(admin.getEmail());
        dto.setPhoneNumber(admin.getPhoneNumber());
        dto.setProfilePhotoPath(admin.getProfilePhotoPath());
        dto.setBlocked(false); // Administrators are never blocked
        dto.setBlockReason(null);
        return dto;
    }

}