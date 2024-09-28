package com.phs.application.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class GetOrderStatusRequest {
    @JsonProperty("status")
    private int status;
    @JsonProperty("shipper_id")
    private Long shipperId;
}