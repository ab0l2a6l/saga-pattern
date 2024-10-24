package org.example.service;


import org.example.dto.DepositRequest;

public interface DepositService {

    void createDeposit(DepositRequest depositRequest);
}
