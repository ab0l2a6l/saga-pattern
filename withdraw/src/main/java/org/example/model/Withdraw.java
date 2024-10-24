package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String amount;
    private boolean isSuccess;
    private String productName;
}
