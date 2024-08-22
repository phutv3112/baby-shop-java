package com.phs.application.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OrderResponse {
    private String billCode;
    private String fullName;
    private int quantity;
    private Date createdAt;
    private long totalPrice;
    private String status;
}
