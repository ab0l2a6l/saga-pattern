package org.example.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("WITHDRAW")
public interface WithdrawClient {
    @PostMapping("/api/v1/withdraw/refund")
    String refund(@RequestParam Long depositId, @RequestParam String amount);

}
