package com.backend.lavugio.service.user.impl;

import com.backend.lavugio.model.user.BlockableAccount;
import com.backend.lavugio.repository.user.BlockableAccountRepository;
import com.backend.lavugio.service.user.BlockableAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlockableAccountServiceImpl implements BlockableAccountService {

    @Autowired
    private BlockableAccountRepository blockableAccountRepository;

    @Override
    @Transactional
    public BlockableAccount blockAccount(Long accountId, String reason) {
        BlockableAccount account = blockableAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("BlockableAccount not found with id: " + accountId));

        account.setBlocked(true);
        account.setBlockReason(reason);
        return blockableAccountRepository.save(account);
    }

    @Override
    @Transactional
    public BlockableAccount unblockAccount(Long accountId) {
        BlockableAccount account = blockableAccountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("BlockableAccount not found with id: " + accountId));

        account.setBlocked(false);
        account.setBlockReason(null);
        return blockableAccountRepository.save(account);
    }

    @Override
    public List<BlockableAccount> getBlockedAccounts() {
        return blockableAccountRepository.findByBlockedTrue();
    }

    @Override
    public List<BlockableAccount> getUnblockedAccounts() {
        return blockableAccountRepository.findByBlockedFalse();
    }

    @Override
    public long countBlockedAccounts() {
        return blockableAccountRepository.countByBlockedTrue();
    }
}