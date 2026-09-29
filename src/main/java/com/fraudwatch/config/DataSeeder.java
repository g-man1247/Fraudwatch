package com.fraudwatch.config;

import com.fraudwatch.model.Rule;
import com.fraudwatch.model.Transaction;
import com.fraudwatch.model.enums.RuleType;
import com.fraudwatch.model.enums.TransactionStatus;
import com.fraudwatch.model.enums.TransactionType;
import com.fraudwatch.repository.RuleRepository;
import com.fraudwatch.repository.TransactionRepository;
import com.fraudwatch.model.AppUser;
import com.fraudwatch.repository.AppUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final TransactionRepository transactionRepository;
    private final RuleRepository ruleRepository;
    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (appUserRepository.count() == 0) {
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("password"));
            appUserRepository.save(admin);
        }

        // Only seed if the database is empty
        if (transactionRepository.count() > 0) {
            return;
        }

        // Seed a detection rule
        if (ruleRepository.count() == 0) {
            Rule largeAmountRule = new Rule();
            largeAmountRule.setName("High Value Alert");
            largeAmountRule.setType(RuleType.LARGE_AMOUNT);
            largeAmountRule.setThresholdAmount(new BigDecimal("10000"));
            largeAmountRule.setEnabled(true);
            largeAmountRule.setDescription("Flags transactions exceeding $10,000");
            ruleRepository.save(largeAmountRule);
        }

        LocalDateTime now = LocalDateTime.now();

        // --- Seed sample transactions ---
        seed(new BigDecimal("2500.00"), "ACC-001", "ACC-002", TransactionType.OUT, TransactionStatus.COMPLETED, now.minusHours(23));
        seed(new BigDecimal("8750.00"), "ACC-003", "ACC-001", TransactionType.IN,  TransactionStatus.COMPLETED, now.minusHours(20));
        seed(new BigDecimal("450.00"),  "ACC-001", "ACC-004", TransactionType.OUT, TransactionStatus.COMPLETED, now.minusHours(18));
        seed(new BigDecimal("12000.00"),"ACC-005", "ACC-001", TransactionType.IN,  TransactionStatus.FLAGGED,   now.minusHours(15));
        seed(new BigDecimal("3200.00"), "ACC-002", "ACC-003", TransactionType.OUT, TransactionStatus.COMPLETED, now.minusHours(12));
        seed(new BigDecimal("15500.00"),"ACC-001", "ACC-006", TransactionType.OUT, TransactionStatus.FLAGGED,   now.minusHours(10));
        seed(new BigDecimal("1800.00"), "ACC-004", "ACC-001", TransactionType.IN,  TransactionStatus.COMPLETED, now.minusHours(8));
        seed(new BigDecimal("920.00"),  "ACC-001", "ACC-005", TransactionType.OUT, TransactionStatus.COMPLETED, now.minusHours(6));
        seed(new BigDecimal("6400.00"), "ACC-007", "ACC-001", TransactionType.IN,  TransactionStatus.COMPLETED, now.minusHours(4));
        seed(new BigDecimal("25000.00"),"ACC-001", "ACC-008", TransactionType.OUT, TransactionStatus.BLOCKED,   now.minusHours(3));
        seed(new BigDecimal("5100.00"), "ACC-009", "ACC-001", TransactionType.IN,  TransactionStatus.COMPLETED, now.minusHours(2));
        seed(new BigDecimal("780.00"),  "ACC-001", "ACC-003", TransactionType.OUT, TransactionStatus.COMPLETED, now.minusHours(1));
    }

    private void seed(BigDecimal amount, String sender, String receiver,
                      TransactionType type, TransactionStatus status, LocalDateTime timestamp) {
        Transaction tx = new Transaction();
        tx.setAmount(amount);
        tx.setSender(sender);
        tx.setReceiver(receiver);
        tx.setType(type);
        tx.setStatus(status);
        tx.setTimestamp(timestamp);
        transactionRepository.save(tx);
    }
}
