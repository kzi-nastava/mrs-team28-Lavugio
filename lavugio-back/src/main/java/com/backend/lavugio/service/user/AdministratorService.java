package com.backend.lavugio.service.user;

import com.backend.lavugio.dto.user.AdministratorDTO;
import com.backend.lavugio.dto.user.AdministratorRegistrationDTO;
import com.backend.lavugio.dto.user.UpdateAdministratorDTO;
import com.backend.lavugio.dto.user.UserProfileDTO;
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

    // Registration
    void register(AdministratorRegistrationDTO request);

    // CRUD Operations
    AdministratorDTO getAdministratorDTOById(Long id);
    AdministratorDTO getAdministratorDTOByEmail(String email);
    List<AdministratorDTO> getAllAdministratorsDTO();
    AdministratorDTO updateAdministratorDTO(Long id, UserProfileDTO request);

    // Profile
    AdministratorDTO getAdministratorProfileDTO();
}