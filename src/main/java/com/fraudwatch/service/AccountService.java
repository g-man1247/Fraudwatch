package com.fraudwatch.service;

import com.fraudwatch.model.Account;
import com.fraudwatch.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    @Transactional
    public void transfer(String senderStr, String receiverStr, BigDecimal amount) {
        Account sender = accountRepository.findByAccountNumber(senderStr)
                .orElseGet(() -> createAccount(senderStr, new BigDecimal("1000000"))); // Mock initial balance
        Account receiver = accountRepository.findByAccountNumber(receiverStr)
                .orElseGet(() -> createAccount(receiverStr, BigDecimal.ZERO));

        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountRepository.save(sender);
        accountRepository.save(receiver);
    }

    private Account createAccount(String accountNumber, BigDecimal initialBalance) {
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setBalance(initialBalance);
        return accountRepository.save(account);
    }
}
