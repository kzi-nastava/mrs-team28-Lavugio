package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.exception.InvalidCredentialsException;
import com.backend.lavugio.exception.UserNotFoundException;
import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.repository.user.AccountRepository;
import com.backend.lavugio.service.user.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public Account updateAccount(Long id, Account account) {
        logger.info("Updating account with id: {}", id);
        
        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Account not found with id: {}", id);
                    return new UserNotFoundException("Account not found with id: " + id);
                });

        existing.setName(account.getName());
        existing.setLastName(account.getLastName());
        existing.setPhoneNumber(account.getPhoneNumber());
        existing.setProfilePhotoPath(account.getProfilePhotoPath());
        existing.setAddress(account.getAddress());

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
    public Optional<Account> getAccountById(Long id) {
        logger.debug("Fetching account with id: {}", id);
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        logger.debug("Fetching account by email: {}", email);
        return accountRepository.findByEmail(email);
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
    public Account changePassword(Long accountId, String newPassword) {
        logger.info("Changing password for account id: {}", accountId);
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    logger.error("Account not found with id: {}", accountId);
                    return new UserNotFoundException("Account not found with id: " + accountId);
                });

        // Hash new password
        account.setPassword(passwordEncoder.encode(newPassword));
        Account updatedAccount = accountRepository.save(account);
        logger.info("Password changed successfully for account id: {}", accountId);
        return updatedAccount;
    }

    @Override
    @Transactional
    public Account updateProfilePhoto(Long accountId, String photoPath) {
        logger.info("Updating profile photo for account id: {}", accountId);
        
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    logger.error("Account not found with id: {}", accountId);
                    return new UserNotFoundException("Account not found with id: " + accountId);
                });

        account.setProfilePhotoPath(photoPath);
        Account updatedAccount = accountRepository.save(account);
        logger.info("Profile photo updated successfully for account id: {}", accountId);
        return updatedAccount;
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
}