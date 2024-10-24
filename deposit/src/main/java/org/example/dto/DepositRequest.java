package org.example.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class DepositRequest implements Serializable {
    private String amount;
    private String productName;
}
