package com.phs.application.service.impl;

import com.phs.application.entity.Shipper;
import com.phs.application.entity.User;
import com.phs.application.exception.BadRequestException;
import com.phs.application.model.dto.SignInDTO;
import com.phs.application.model.dto.UserDTO;
import com.phs.application.model.mapper.ShipperMapper;
import com.phs.application.model.mapper.UserMapper;
import com.phs.application.model.request.*;
import com.phs.application.repository.ShipperRepository;
import com.phs.application.repository.UserRepository;
import com.phs.application.service.ShipperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.phs.application.config.Contant.LIMIT_USER;
@Service
public class ShipperServiceImpl implements ShipperService {
    @Autowired
    private ShipperRepository shipperRepository;
    @Override
    public Page<Shipper> adminListShipperPages(String username, String phone, Integer page) {
        page--;
        if (page < 0) {
            page = 0;
        }
        Pageable pageable = PageRequest.of(page, LIMIT_USER, Sort.by("created_at").descending());
        return shipperRepository.adminListShipperPages(username, phone, pageable);
    }

    @Override
    public Shipper createShipper(CreateShipperRequest createShipperRequest) {
        Shipper shipper = shipperRepository.findByUsername(createShipperRequest.getUsername());
        if (shipper != null) {
            throw new BadRequestException("Username đã tồn tại trong hệ thống. Vui lòng sử dụng username khác!");
        }
        shipper = ShipperMapper.toShipper(createShipperRequest);
        shipperRepository.save(shipper);
        return shipper;
    }

    @Override
    public void updateShipper(Long id, Shipper shipper){
        Optional<Shipper> existingUserOpt = shipperRepository.findById(id);

        if (existingUserOpt.isPresent()) {
            Shipper existingUser = existingUserOpt.get();

            // Cập nhật thông tin từ form
            existingUser.setUsername(shipper.getUsername());
            existingUser.setPhone(shipper.getPhone());
            existingUser.setAddress(shipper.getAddress());

            // Lưu user đã cập nhật lại vào database
            shipperRepository.save(existingUser);
        }
    }
    @Override
    public Shipper findById(Long id) {
        Optional<Shipper> shipper = shipperRepository.findById(id);
        return shipper.orElse(null);
    }
    @Override
    public void deleteShipperById(Long id) {
        Shipper shipper = shipperRepository.findById(id).orElse(null);
        if (shipper != null) {
            shipperRepository.delete(shipper);
        }else {
            throw new BadRequestException("Shipper not found");
        }

    }
    @Override
    public SignInDTO signIn(String username, String password){
        Shipper shipper = shipperRepository.findByUsername(username);
        if(shipper == null){
            return null;
        }
        if (shipper.getPassword().equals(password)) {
            SignInDTO signInDTO = new SignInDTO();
            signInDTO.setUsername(username);
            signInDTO.setId(shipper.getId());
            signInDTO.setPhone(shipper.getPhone());
            signInDTO.setAddress(shipper.getAddress());
            return signInDTO;
        }else {
            return null;
        }
    }
}
