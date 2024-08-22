package com.phs.application.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.phs.application.entity.Product;
import com.phs.application.entity.ProductSize;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class CreateOrderRequestV2 {

    @JsonProperty("products")
    private List<ProductSize> products;


    @JsonProperty("receiver_name")
    private String receiverName;

    @JsonProperty("receiver_phone")
    private String receiverPhone;

    @JsonProperty("receiver_address")
    private String receiverAddress;

    @JsonProperty("coupon_code")
    private String couponCode;

    @JsonProperty("total_price")
    private long totalPrice;

    @JsonProperty("product_price")
    private long productPrice;

    private String note;

    private long userId;

}
