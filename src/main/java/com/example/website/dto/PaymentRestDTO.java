package com.example.website.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class PaymentRestDTO  implements Serializable {
    private String status;
    private String message;
    private String URL;

}
