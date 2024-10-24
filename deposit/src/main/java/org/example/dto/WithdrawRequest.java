package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class WithdrawRequest implements Serializable {
    private Long depositId;
    private String amount;
    private String productName;
}
