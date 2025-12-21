package com.backend.lavugio.service.user;

import com.backend.lavugio.model.user.Account;

import java.util.List;
import java.util.Optional;

public interface AccountService {
    Account createAccount(Account account);
    Account updateAccount(Long id, Account account);
    void deleteAccount(Long id);
    Optional<Account> getAccountById(Long id);
    Optional<Account> getAccountByEmail(String email);
    List<Account> getAllAccounts();
    boolean accountExistsByEmail(String email);
    Account changePassword(Long accountId, String newPassword);
    Account updateProfilePhoto(Long accountId, String photoPath);
    Account authenticate(String email, String password);
}