package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.user.Account;
import com.backend.lavugio.repository.user.AccountRepository;
import com.backend.lavugio.service.user.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

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
    public Optional<Account> getAccountById(Long id) {
        return accountRepository.findById(id);
    }

    @Override
    public Optional<Account> getAccountByEmail(String email) {
        return accountRepository.findByEmail(email);
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
    public Account changePassword(Long accountId, String newPassword) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        account.setPassword(newPassword);
        return accountRepository.save(account);
    }

    @Override
    @Transactional
    public Account updateProfilePhoto(Long accountId, String photoPath) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found with id: " + accountId));

        account.setProfilePhotoPath(photoPath);
        return accountRepository.save(account);
    }

    @Override
    public Account authenticate(String email, String password) {
        return accountRepository.findByEmailAndPassword(email, password)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
    }
}