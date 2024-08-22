package com.phs.application.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDetailResponse {

    private String billCode;
    private long prices;
    private String receiverName;
    private String receiverAddress;
    private String receiverPhone;
    private int quantity;
    private String note;
    private String productId;
}
