package org.example.controller;

import org.example.dto.DepositRequest;
import org.example.service.DepositService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/deposit/")
public class DepositController {

    private final DepositService orderService;

    @Autowired
    public DepositController(DepositService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/create")
    public void saveOrder(@RequestBody DepositRequest depositRequest) {
        orderService.createDeposit(depositRequest);
    }
}
