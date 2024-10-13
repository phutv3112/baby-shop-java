package com.phs.application.controller.admin;

import com.phs.application.entity.Shipper;
import com.phs.application.entity.User;
import com.phs.application.model.request.CreateShipperRequest;
import com.phs.application.model.request.CreateUserRequestDto;
import com.phs.application.service.ShipperService;
import com.phs.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
public class ShipperController {
    @Autowired
    private ShipperService shipperService;

    @GetMapping("/admin/shippers")
    public String homePages(Model model,
                            @RequestParam(defaultValue = "", required = false) String username,
                            @RequestParam(defaultValue = "", required = false) String phone,
                            @RequestParam(defaultValue = "", required = false) String address,
                            @RequestParam(defaultValue = "1", required = false) Integer page) {
        Page<Shipper> shippers = shipperService.adminListShipperPages(username, phone, page);
        model.addAttribute("shippers", shippers.getContent());
        model.addAttribute("totalPages", shippers.getTotalPages());
        model.addAttribute("currentPage", shippers.getPageable().getPageNumber() + 1);
        return "admin/shipper/list";
    }


    @GetMapping("/admin/shipper/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("createShipperRequest", new CreateShipperRequest());
        return "admin/shipper/create";
    }
    @PostMapping("/admin/shipper/create")
    public String createShipper(
            @Valid @ModelAttribute("createShipperRequest") CreateShipperRequest createShipperRequest,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "admin/shipper/create";
        }
        model.addAttribute("createShipperRequest", createShipperRequest);
        shipperService.createShipper(createShipperRequest);

        return "redirect:/admin/shippers"; // Chuyển hướng sau khi tạo user thành công
    }

    @DeleteMapping("/admin/shipper/{id}")
    public ResponseEntity<Object> deleteShipper(@PathVariable Long id) {
        shipperService.deleteShipperById(id);
        return ResponseEntity.ok("Xoá shipper thành công!");
    }

    @GetMapping("/admin/shipper/{id}")
    public String showEditShipperForm(@PathVariable Long id, Model model) {
        Shipper shipper = shipperService.findById(id);
        model.addAttribute("shipper", shipper);
        return "admin/shipper/edit";
    }
    @PostMapping("/admin/shipper/edit/{id}")
    public String updateShipper(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("shipper") Shipper shipper,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "admin/shipper/edit";
        }
        shipperService.updateShipper(id, shipper);

        return "redirect:/admin/shippers";
    }
    @GetMapping("/admin/shipper/location/{id}")
    public String locationShipper(
            @PathVariable("id") Long id, Model model) {

        model.addAttribute("shipperId", id);
        return  "admin/shipper/location";
    }
}
