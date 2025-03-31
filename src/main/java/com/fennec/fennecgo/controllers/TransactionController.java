package com.fennec.fennecgo.controllers;

import com.fennec.fennecgo.dto.response.MyActivityResponse;
import com.fennec.fennecgo.services.Interface.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/my-activity")
    public MyActivityResponse getMyActivity() {
        return transactionService.getMyActivity();
    }
}