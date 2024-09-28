package com.phs.application.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.phs.application.entity.ProductSize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ChangeOrderStatusRequest {
    @JsonProperty("status")
    private int status;
    @JsonProperty("shipper_id")
    private Long shipperId;
    @JsonProperty("order_id")
    private long orderId;
}
