package com.backend.lavugio.service.user;

import com.backend.lavugio.model.user.Administrator;

import java.util.List;
import java.util.Optional;

public interface AdministratorService {
    Administrator createAdministrator(Administrator admin);
    Administrator updateAdministrator(Long id, Administrator admin);
    void deleteAdministrator(Long id);
    Administrator getAdministratorById(Long id);
    Optional<Administrator> getAdministratorByEmail(String email);
    List<Administrator> getAllAdministrators();
}