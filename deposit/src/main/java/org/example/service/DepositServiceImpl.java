package org.example.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.client.WithdrawClient;
import org.example.dto.DepositRequest;
import org.example.dto.WithdrawRequest;
import org.example.dto.WithdrawResponse;
import org.example.model.Deposit;
import org.example.repository.DepositRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DepositServiceImpl implements DepositService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final DepositRepository depositRepository;
    private final WithdrawClient withdrawClient;

    @Autowired
    public DepositServiceImpl(KafkaTemplate<String, String> kafkaTemplate, DepositRepository depositRepository, WithdrawClient withdrawClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.depositRepository = depositRepository;
        this.withdrawClient = withdrawClient;
    }

    @Override
    public void createDeposit(DepositRequest depositRequest) {
        Deposit deposit = new Deposit();
        deposit.setAmount(depositRequest.getAmount());
        deposit.setState("WAITING FOR PAYMENT");
        Deposit savedDeposit = depositRepository.save(deposit);
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        withdrawRequest.setDepositId(savedDeposit.getId());
        withdrawRequest.setAmount(savedDeposit.getAmount());
        withdrawRequest.setProductName(depositRequest.getProductName());
        Gson gson = new GsonBuilder().create();
        String request = gson.toJson(withdrawRequest, WithdrawRequest.class);
        kafkaTemplate.send("withdraw-request-topic", request);
    }

    @KafkaListener(topics = "payment-response-topic", groupId = "saga-group", containerFactory = "orderListener")
    public void handlePaymentResponse(String response) {
        Gson gson = new GsonBuilder().create();
        WithdrawResponse withdrawResponse = gson.fromJson(response, WithdrawResponse.class);
        if (withdrawResponse.isSuccess()) {
            updateOrderState(withdrawResponse.getDepositId(), "COMPLETED");
        } else {
            updateOrderState(withdrawResponse.getDepositId(), "FAILED");
            handleCompensation(withdrawResponse.getDepositId(), withdrawResponse.getAmount());
        }
    }

    private void handleCompensation(Long orderId, String amount) {
        withdrawClient.refund(orderId, amount);
    }

    private void updateOrderState(Long orderId, String state) {
        Optional<Deposit> deposit = depositRepository.findById(orderId);
        if (deposit.isPresent()) {
            deposit.get().setState(state);
            depositRepository.save(deposit.get());
        }
    }
}