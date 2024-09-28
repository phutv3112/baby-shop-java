package com.phs.application.model.mapper;

import com.phs.application.entity.Shipper;
import com.phs.application.entity.User;
import com.phs.application.model.dto.ShipperDTO;
import com.phs.application.model.dto.UserDTO;
import com.phs.application.model.request.CreateShipperRequest;
import com.phs.application.model.request.CreateUserRequest;
import com.phs.application.model.request.CreateUserRequestDto;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

public class ShipperMapper {
    public static ShipperDTO toShipperDTO(Shipper shipper) {
        ShipperDTO shipperDTO = new ShipperDTO();
        shipperDTO.setId(shipper.getId());
        shipperDTO.setUsername(shipper.getUsername());
        shipperDTO.setAddress(shipper.getAddress());
        shipperDTO.setPhone(shipper.getPhone());

        return shipperDTO;
    }

    public static Shipper toShipper(CreateShipperRequest createShipperRequest ) {
        Shipper shipper = new Shipper();
        shipper.setUsername(createShipperRequest.getUsername());
        shipper.setPassword(createShipperRequest.getPassword());
        shipper.setPhone(createShipperRequest.getPhone());
        shipper.setAddress(createShipperRequest.getAddress());
        shipper.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        return shipper;
    }

}
