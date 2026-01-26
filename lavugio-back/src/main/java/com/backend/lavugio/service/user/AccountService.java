package com.backend.lavugio.service.user;

import com.backend.lavugio.dto.user.AccountUpdateDTO;
import com.backend.lavugio.dto.user.BlockUserDTO;
import com.backend.lavugio.dto.user.CanOrderRideDTO;
import com.backend.lavugio.dto.user.IsAccountBlockedDTO;
import com.backend.lavugio.model.user.Account;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface AccountService {
    Account createAccount(Account account);
    Account updateAccount(Long id, AccountUpdateDTO accountUpdate);
    void deleteAccount(Long id);
    Account getAccountById(Long id);
    Account getAccountByEmail(String email);
    List<Account> getAllAccounts();
    boolean accountExistsByEmail(String email);
    Account changePassword(Long accountId, String oldPassword, String newPassword);
    Account updateProfilePhoto(Long accountId, MultipartFile file);
    Resource getProfilePhoto(Long accountId);
    Account authenticate(String email, String password);
    List<String> findTop5EmailsByPrefix(String prefix, Pageable pageable);
    void blockUser(BlockUserDTO blockUserDTO);
    IsAccountBlockedDTO isBlocked(Long userId);
    CanOrderRideDTO canOrderRide(Long userId);
}