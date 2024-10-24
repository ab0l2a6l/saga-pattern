package org.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DepositResponse implements Serializable {
    private Long depositId;
    private String amount;
    private String productName;
}
