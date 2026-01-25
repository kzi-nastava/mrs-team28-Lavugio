package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.dto.user.AccountUpdateDTO;
import com.backend.lavugio.exception.InvalidCredentialsException;
import com.backend.lavugio.exception.UserNotFoundException;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.repository.user.AccountRepository;
import com.backend.lavugio.service.user.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String uploadDir = "uploads/profile-photos/";
    private final String defaultPhotoUrl = "uploads/profile-photos/default_avatar_photo.jpg";

    @Override
    @Transactional
    public Account createAccount(Account account) {
        logger.info("Creating account for email: {}", account.getEmail());
        
        // Hash password before saving
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        account.setEmailVerified(false);
        
        Account savedAccount = accountRepository.save(account);
        logger.info("Account created successfully with id: {}", savedAccount.getId());
        return savedAccount;
    }

    @Override
    @Transactional
    public Account updateAccount(Long id, AccountUpdateDTO accountUpdate) {
        logger.info("Updating account with id: {}", id);
        
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Account not found with id: {}", id);
                    return new UserNotFoundException("Account not found with id: " + id);
                });

        existing.setName(accountUpdate.getName());
        existing.setLastName(accountUpdate.getSurname());
        existing.setPhoneNumber(accountUpdate.getPhoneNumber());
        //existing.setAddress(accountUpdate.getAddress()); ??

        Account updatedAccount = accountRepository.save(existing);
        logger.info("Account updated successfully with id: {}", id);
        return updatedAccount;
    }

    @Override
    @Transactional
    public void deleteAccount(Long id) {
        logger.info("Deleting account with id: {}", id);
        
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Account not found with id: {}", id);
                    return new UserNotFoundException("Account not found with id: " + id);
                });

        accountRepository.delete(account);
        logger.info("Account deleted successfully with id: {}", id);
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
        logger.debug("Fetching all accounts");
        return accountRepository.findAll();
    }

    @Override
    public boolean accountExistsByEmail(String email) {
        logger.debug("Checking if account exists with email: {}", email);
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
            Path path;

            if (photoPath == null || photoPath.isBlank()) {
                path = Paths.get(defaultPhotoUrl);
            } else {
                path = Paths.get(photoPath);
            }

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
        logger.info("Authenticating user with email: {}", email);
        
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Authentication failed: Account not found with email: {}", email);
                    return new UserNotFoundException("Account not found with email: " + email);
                });

        // Verify password using BCryptPasswordEncoder
        if (!passwordEncoder.matches(password, account.getPassword())) {
            logger.warn("Authentication failed: Invalid password for email: {}", email);
            throw new InvalidCredentialsException("Invalid email or password");
        }

        logger.info("User authenticated successfully: {}", email);
        return account;
    }

    @Override
    public List<String> findTop5EmailsByPrefix(String prefix, Pageable pageable) {
        return this.accountRepository.findTop5EmailsByPrefix(prefix, pageable);
    }
}