package org.example.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class WithdrawResponse implements Serializable {
    private boolean isSuccess;
    private String amount;
    private Long depositId;
}
