package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.WithdrawalRequest;
import com.fennec.fennecgo.dto.response.WithdrawalInitiationResponse;
import com.fennec.fennecgo.dto.response.WithdrawalResponse;
import com.fennec.fennecgo.exception.InsufficientBalanceException;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.*;
import com.fennec.fennecgo.repository.PaymentMethodRepository;
import com.fennec.fennecgo.repository.TransactionRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.repository.WalletRepository;
import com.fennec.fennecgo.services.Interface.FeeRuleService;
import com.fennec.fennecgo.services.Interface.WithdrawalService;
import com.google.zxing.BarcodeFormat;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Base64;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class WithdrawalServiceImpl implements WithdrawalService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final FeeRuleService feeRuleService;

    @Override
    public WithdrawalInitiationResponse initiateWithdrawal(WithdrawalRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be greater than zero");
        }

        Wallet wallet = walletRepository.findById(request.getWalletId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        // For withdrawal, transaction type should be "WITHDRAWAL"
        TransactionType withdrawalType = transactionTypeRepository.findByCode("WITHDRAWAL")
                .orElseThrow(() -> new ResourceNotFoundException("TransactionType 'WITHDRAWAL' not found"));

        PaymentMethod pm = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found"));

        // Calculate fee (if applicable)
        BigDecimal fee = feeRuleService.calculateFee(withdrawalType, request.getAmount());
        BigDecimal totalAmount = request.getAmount().add(fee);

        // Check wallet balance
        if (wallet.getBalance().compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance for withdrawal");
        }

        // Create new withdrawal transaction with status PENDING
        Transaction transaction = new Transaction();
        transaction.setTransactionType(withdrawalType);
        transaction.setFromWallet(wallet);
        transaction.setToWallet(null); // External transfer
        transaction.setPaymentMethod(pm);
        transaction.setAmount(request.getAmount());
        transaction.setFee(fee);
        transaction.setStatus(TransactionStatus.PENDING);
        // Omit description as requested

        String refPrefix = pm.getType().toUpperCase() + "_";
        String referenceNumber = refPrefix + UUID.randomUUID();
        transaction.setReferenceNumber(referenceNumber);

        transactionRepository.save(transaction);

        String qrCodeBase64 = null;
        // For withdrawals via agent, generate QR code if needed
        if (pm.getType().equalsIgnoreCase("AGENT")) {
            qrCodeBase64 = generateQrCodeBase64(referenceNumber);
        }

        WithdrawalInitiationResponse response = new WithdrawalInitiationResponse();
        response.setTransactionId(transaction.getId());
        response.setReferenceNumber(referenceNumber);
        response.setAmount(request.getAmount());
        response.setFee(fee);
        response.setPaymentMethod(pm.getType());
        response.setStatus(TransactionStatus.PENDING.name());
        response.setQrCodeBase64(qrCodeBase64);
        response.setCreatedAt(transaction.getCreatedAt());

        return response;
    }

    @Override
    public WithdrawalResponse cancelWithdrawalByReference(String referenceNumber) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with reference: " + referenceNumber));
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel a transaction that is not PENDING");
        }
        transaction.setStatus(TransactionStatus.CANCELED);
        transactionRepository.save(transaction);

        WithdrawalResponse response = new WithdrawalResponse();
        response.setTransactionId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setFee(transaction.getFee());
        response.setTotalCharge(transaction.getAmount().add(transaction.getFee()));
        response.setPaymentMethod(transaction.getPaymentMethod().getType());
        response.setStatus(transaction.getStatus().name());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setCreatedAt(transaction.getCreatedAt());
        response.setNewBalance(transaction.getFromWallet().getBalance());
        return response;
    }

    @Override
    public WithdrawalResponse confirmWithdrawalByReference(String referenceNumber) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with reference: " + referenceNumber));
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Cannot confirm a transaction that is not PENDING");
        }
        Wallet wallet = transaction.getFromWallet();
        BigDecimal totalAmount = transaction.getAmount().add(transaction.getFee());

        if (wallet.getBalance().compareTo(totalAmount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance on confirmation");
        }
        wallet.setBalance(wallet.getBalance().subtract(totalAmount));
        walletRepository.save(wallet);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        WithdrawalResponse response = new WithdrawalResponse();
        response.setTransactionId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setFee(transaction.getFee());
        response.setTotalCharge(transaction.getAmount().add(transaction.getFee()));
        response.setNewBalance(wallet.getBalance());
        response.setPaymentMethod(transaction.getPaymentMethod().getType());
        response.setStatus(transaction.getStatus().name());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }

    private String generateQrCodeBase64(String reference) {
        try {
            int width = 250, height = 250;
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(reference, BarcodeFormat.QR_CODE, width, height);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR Code", e);
        }
    }
}
