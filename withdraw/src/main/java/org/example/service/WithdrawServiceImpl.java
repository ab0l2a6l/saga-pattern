package org.example.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.example.dto.WithdrawRequest;
import org.example.dto.WithdrawResponse;
import org.example.model.Withdraw;
import org.example.repository.WithdrawRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class WithdrawServiceImpl implements WithdrawService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final WithdrawRepository withdrawRepository;

    @Autowired
    public WithdrawServiceImpl(KafkaTemplate<String, String> kafkaTemplate, WithdrawRepository withdrawRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.withdrawRepository = withdrawRepository;
    }

    @KafkaListener(topics = "withdraw-request-topic", groupId = "saga-group", containerFactory = "withdrawListener")
    public void refundWithdraw(String request) {
        Gson gson = new GsonBuilder().create();
        WithdrawRequest withdrawRequest = gson.fromJson(request, WithdrawRequest.class);
        Withdraw withdraw = new Withdraw();
        WithdrawResponse withdrawResponse = new WithdrawResponse();
        if (withdrawRequest.getProductName().isEmpty()) {
            withdrawResponse.setDepositId(withdrawRequest.getDepositId());
            withdrawResponse.setAmount(withdrawRequest.getAmount());
            withdrawResponse.setSuccess(Boolean.FALSE);
        } else {
            withdraw.setAmount(withdrawRequest.getAmount());
            withdraw.setSuccess(Boolean.TRUE);
            withdraw.setProductName(withdrawRequest.getProductName());
            withdrawRepository.save(withdraw);
            withdrawResponse.setDepositId(withdrawRequest.getDepositId());
            withdrawResponse.setAmount(withdrawRequest.getAmount());
            withdrawResponse.setSuccess(Boolean.TRUE);
        }
        String response = gson.toJson(withdrawResponse, withdrawResponse.getClass());
        kafkaTemplate.send("payment-response-topic", response);
    }

    @Override
    public String refundWithdraw(Long depositId, String amount) {
        return "refund withdraw success with deposit id " + depositId + " and amount " + amount;
    }
}
