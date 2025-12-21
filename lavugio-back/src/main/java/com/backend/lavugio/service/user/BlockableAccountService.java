package com.backend.lavugio.service.user;

import com.backend.lavugio.model.user.BlockableAccount;

import java.util.List;

public interface BlockableAccountService {
    BlockableAccount blockAccount(Long accountId, String reason);
    BlockableAccount unblockAccount(Long accountId);
    List<BlockableAccount> getBlockedAccounts();
    List<BlockableAccount> getUnblockedAccounts();
    long countBlockedAccounts();
}