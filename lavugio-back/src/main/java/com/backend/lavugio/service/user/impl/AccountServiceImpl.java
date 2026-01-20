package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.repository.user.AccountRepository;
import com.backend.lavugio.service.user.AccountService;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String uploadDir = "uploads/profile-photos/";
    private final String defaultPhotoUrl = "uploads/profile-photos/default_avatar_photo.jpg";

    @Override
    @Transactional
    public Account createAccount(Account account) {
        // Provera da li email već postoji
        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new RuntimeException("Email already exists: " + account.getEmail());
        }

        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account updateAccount(Long id, Account account) {
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        // Provera da li se email menja i da li novi email već postoji
        if (!existing.getEmail().equals(account.getEmail()) &&
                accountRepository.existsByEmail(account.getEmail())) {
            throw new RuntimeException("Email already exists: " + account.getEmail());
        }

        existing.setName(account.getName());
        existing.setLastName(account.getLastName());
        existing.setEmail(account.getEmail());
        existing.setPassword(account.getPassword());
        existing.setProfilePhotoPath(account.getProfilePhotoPath());

        return accountRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + id));

        accountRepository.delete(account);
    }

    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Account not found with id :" + id));
    }

    @Override
    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("Account with email: " + email + " was not found"));
    }

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Override
    public boolean accountExistsByEmail(String email) {
        return accountRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public Account changePassword(Long accountId, String oldPassword, String newPassword) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        if (passwordEncoder.matches(oldPassword, account.getPassword())) {
            String hashedPassword = passwordEncoder.encode(newPassword);
            account.setPassword(hashedPassword);
            return accountRepository.save(account);
        } else {
            throw new RuntimeException("Old password is incorrect.");
        }
    }

    @Override
    @Transactional
    public Account updateProfilePhoto(Long accountId, MultipartFile file) {
        try {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new RuntimeException("Account not found"));

            String contentType = file.getContentType();

            if (!contentType.equals("image/jpeg") &&
                    !contentType.equals("image/png")) {
                throw new RuntimeException("Only JPG and PNG allowed");
            }

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            if (account.getProfilePhotoPath() != null) {
                Files.deleteIfExists(Paths.get(account.getProfilePhotoPath()));
            }

            String originalFilename = file.getOriginalFilename();
            String exstension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = "user_" + account.getId() + "_" + System.currentTimeMillis() + exstension;
            String fullpath = uploadDir + fileName;
            Path path = Paths.get(fullpath);
            Files.write(path, file.getBytes());

            account.setProfilePhotoPath(fullpath);
            accountRepository.save(account);

            return account;
        } catch (IOException e) {
            throw new RuntimeException("Error saving file", e);
        }

    }

    @Override
    public Resource getProfilePhoto(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account with id " + accountId + " not found."));

        String photoPath = account.getProfilePhotoPath();

        try {
            if (photoPath == null || photoPath.isBlank()) {
                return new UrlResource(defaultPhotoUrl);
            }

            Path path = Paths.get(photoPath);

            if (!Files.exists(path)) {
                return null;
            }

            return new UrlResource(path.toUri());

        } catch (MalformedURLException e) {
            throw new RuntimeException("Error loading photo file", e);
        }
    }

    @Override
    public Account authenticate(String email, String password) {
        return accountRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    }
}