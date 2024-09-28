package com.phs.application.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ShipperDTO {
    private long id;
    private String username;
    private String address;
    private String phone;
}
