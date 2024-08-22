package com.phs.application.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CartResponseDTO {
    private Long cartId;
    private String id;
    private String name;
    private String slug;
    private long price;
    private int quantity;
    private ArrayList<String> images;
    private Long totalSold;
    private Long total_money;
}
