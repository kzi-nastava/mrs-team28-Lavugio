package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.user.Administrator;
import com.backend.lavugio.repository.user.AdministratorRepository;
import com.backend.lavugio.service.user.AdministratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
}