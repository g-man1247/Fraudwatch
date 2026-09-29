package com.fraudwatch.service;

import com.fraudwatch.dto.TransactionRequest;
import com.fraudwatch.model.Transaction;
import com.fraudwatch.model.enums.TransactionStatus;
import com.fraudwatch.model.enums.TransactionType;
import com.fraudwatch.repository.FlaggedTransactionRepository;
import com.fraudwatch.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    
    @Mock
    private FraudDetectionService fraudDetectionService;
    
    @Mock
    private FlaggedTransactionRepository flaggedTransactionRepository;
    
    @Mock
    private AccountService accountService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void testNormalTransactionCompletes() {
        TransactionRequest request = new TransactionRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setSender("Alice");
        request.setReceiver("Bob");
        request.setType(TransactionType.IN);

        Transaction savedTx = new Transaction();
        savedTx.setId(1L);
        savedTx.setAmount(request.getAmount());
        savedTx.setSender(request.getSender());
        savedTx.setReceiver(request.getReceiver());
        savedTx.setType(TransactionType.IN);
        
        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTx);
        when(fraudDetectionService.evaluateTransaction(any())).thenReturn(Collections.emptyList());

        Transaction result = transactionService.processTransaction(request);

        assertEquals(TransactionStatus.COMPLETED, result.getStatus());
        verify(accountService, times(1)).transfer(eq("Alice"), eq("Bob"), eq(new BigDecimal("100.00")));
    }
}
