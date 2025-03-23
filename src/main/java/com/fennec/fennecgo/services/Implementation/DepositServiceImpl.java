package com.fennec.fennecgo.services.Implementation;

import com.fennec.fennecgo.dto.request.DepositRequest;
import com.fennec.fennecgo.dto.response.DepositInitiationResponse;
import com.fennec.fennecgo.dto.response.DepositResponse;
import com.fennec.fennecgo.exception.ResourceNotFoundException;
import com.fennec.fennecgo.models.*;
import com.fennec.fennecgo.repository.PaymentMethodRepository;
import com.fennec.fennecgo.repository.TransactionRepository;
import com.fennec.fennecgo.repository.TransactionTypeRepository;
import com.fennec.fennecgo.repository.WalletRepository;
import com.fennec.fennecgo.services.Interface.DepositService;
import com.fennec.fennecgo.services.Interface.FeeRuleService;
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
public class DepositServiceImpl implements DepositService {

    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final FeeRuleService feeRuleService;

    @Override
    public DepositInitiationResponse initiateDeposit(DepositRequest request) {
        // Validate deposit amount
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero");
        }

        // Find wallet to be credited
        Wallet wallet = walletRepository.findById(request.getWalletId())
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with id: " + request.getWalletId()));

        // Get transaction type "DEPOSIT"
        TransactionType depositType = transactionTypeRepository.findByCode("DEPOSIT")
            .orElseThrow(() -> new ResourceNotFoundException("TransactionType with code 'DEPOSIT' not found"));

        // Look up PaymentMethod by ID
        PaymentMethod pm = paymentMethodRepository.findById(request.getPaymentMethodId())
            .orElseThrow(() -> new ResourceNotFoundException("PaymentMethod not found with id: " + request.getPaymentMethodId()));

        // Create a new deposit transaction with status PENDING
        Transaction transaction = new Transaction();
        transaction.setTransactionType(depositType);
        transaction.setFromWallet(null); // For deposit, no originating wallet
        transaction.setToWallet(wallet);
        transaction.setPaymentMethod(pm);
        transaction.setAmount(request.getAmount());
        transaction.setStatus(TransactionStatus.PENDING);
        // Omit description per request

        // Generate reference number (e.g., "CIB_EDAHABIA_...")
        String refPrefix = pm.getType().toUpperCase() + "_";
        String referenceNumber = refPrefix + UUID.randomUUID();
        transaction.setReferenceNumber(referenceNumber);

        // Calculate fee using FeeRuleService
        BigDecimal fee = feeRuleService.calculateFee(depositType, request.getAmount());
        transaction.setFee(fee);

        // Save transaction (transaction ID will be generated)
        transactionRepository.save(transaction);

        // Generate QR code only if needed (e.g., for agent flows). For other methods, this might be null.
        String qrCodeBase64 = null;
        if (pm.getType().equalsIgnoreCase("AGENT")) {
            qrCodeBase64 = generateQrCodeBase64(referenceNumber);
        }

        // Build and return the initiation response
        DepositInitiationResponse response = new DepositInitiationResponse();
        response.setTransactionId(transaction.getId());
        response.setReferenceNumber(referenceNumber);
        response.setAmount(request.getAmount());
        response.setFee(fee);
        response.setPaymentMethod(pm.getType());
        response.setStatus(TransactionStatus.PENDING.name());
        response.setQrCodeBase64(qrCodeBase64);
        return response;
    }

    @Override
    public DepositResponse cancelDepositByReference(String referenceNumber) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with reference: " + referenceNumber));
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Cannot cancel a transaction that is not PENDING");
        }
        transaction.setStatus(TransactionStatus.CANCELED);
        transactionRepository.save(transaction);

        DepositResponse response = new DepositResponse();
        response.setTransactionId(transaction.getId());
        response.setAmount(transaction.getAmount());
        response.setFee(transaction.getFee());
        response.setTotalCharge(transaction.getAmount().add(transaction.getFee()));
        response.setPaymentMethod(transaction.getPaymentMethod().getType());
        response.setStatus(transaction.getStatus().name());
        response.setReferenceNumber(transaction.getReferenceNumber());
        response.setCreatedAt(transaction.getCreatedAt());
        return response;
    }

    @Override
    public DepositResponse confirmDepositByReference(String referenceNumber) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with reference: " + referenceNumber));
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalStateException("Cannot confirm a transaction that is not PENDING");
        }
        // Mark transaction as SUCCESS
        transaction.setStatus(TransactionStatus.SUCCESS);

        // Credit the wallet
        Wallet wallet = transaction.getToWallet();
        if (wallet == null) {
            throw new ResourceNotFoundException("No wallet found to credit");
        }
        wallet.setBalance(wallet.getBalance().add(transaction.getAmount()));
        walletRepository.save(wallet);

        transactionRepository.save(transaction);

        DepositResponse response = new DepositResponse();
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
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(reference, BarcodeFormat.QR_CODE, width, height);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            return Base64.getEncoder().encodeToString(imageBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR Code", e);
        }
    }
}
