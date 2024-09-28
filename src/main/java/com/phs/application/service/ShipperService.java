package com.phs.application.service;

import com.phs.application.entity.Shipper;
import com.phs.application.entity.User;
import com.phs.application.model.dto.SignInDTO;
import com.phs.application.model.dto.UserDTO;
import com.phs.application.model.request.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ShipperService {
    Page<Shipper> adminListShipperPages(String username, String phone, Integer page);
    Shipper createShipper(CreateShipperRequest createShipperRequest);
    Shipper findById(Long id);
    void updateShipper(Long id, Shipper shipper);
    void deleteShipperById(Long id);
    SignInDTO signIn(String username, String password);
}
