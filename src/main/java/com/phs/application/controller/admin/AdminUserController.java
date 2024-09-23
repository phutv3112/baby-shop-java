package com.phs.application.controller.admin;

import com.phs.application.entity.User;
import com.phs.application.model.dto.UserDTO;
import com.phs.application.model.request.CreateUserRequestDto;
import com.phs.application.model.request.UpdateProfileRequest;
import com.phs.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
public class AdminUserController {

    @Autowired
    private UserService userService;

    @GetMapping("/admin/users")
    public String homePages(Model model,
                            @RequestParam(defaultValue = "", required = false) String fullName,
                            @RequestParam(defaultValue = "", required = false) String phone,
                            @RequestParam(defaultValue = "", required = false) String email,
                            @RequestParam(defaultValue = "", required = false) String address,
                            @RequestParam(defaultValue = "1", required = false) Integer page) {
        Page<User> users = userService.adminListUserPages(fullName, phone, email, page);
        model.addAttribute("users", users.getContent());
        model.addAttribute("totalPages", users.getTotalPages());
        model.addAttribute("currentPage", users.getPageable().getPageNumber() + 1);
        return "admin/user/list";
    }

    @GetMapping("/api/admin/users/list")
    public ResponseEntity<Object> getListUserPages(@RequestParam(defaultValue = "", required = false) String fullName,
                                                   @RequestParam(defaultValue = "", required = false) String phone,
                                                   @RequestParam(defaultValue = "", required = false) String email,
                                                   @RequestParam(defaultValue = "", required = false) String address,
                                                   @RequestParam(defaultValue = "1", required = false) Integer page) {
        Page<User> users = userService.adminListUserPages(fullName, phone, email, page);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/admin/user/create")
    public String showCreateUserForm(Model model) {
        model.addAttribute("createUserRequestDto", new CreateUserRequestDto());
        return "admin/user/create"; // Tên trang hiển thị form tạo user
    }
    @PostMapping("/admin/user/create")
    public String createUser(
            @Valid @ModelAttribute("createUserRequestDto") CreateUserRequestDto createUserRequestDto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "admin/user/create";
        }
        model.addAttribute("createUserRequestDto", createUserRequestDto);
        userService.createNewUser(createUserRequestDto);

        return "redirect:/admin/users"; // Chuyển hướng sau khi tạo user thành công
    }

    @DeleteMapping("/admin/user/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok("Xoá user thành công!");
    }

    @GetMapping("/admin/user/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "admin/user/edit";
    }
    @PostMapping("/admin/user/edit/{id}")
    public String updateUser(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("user") User user,
            BindingResult result,
            Model model) {

        // Kiểm tra xem có lỗi trong quá trình valid dữ liệu hay không
        if (result.hasErrors()) {
            return "admin/user/edit"; // Trả lại form nếu có lỗi
        }

        // Cập nhật thông tin người dùng
        userService.updateUser(id, user);  // Triển khai phương thức update trong service để cập nhật

        return "redirect:/admin/users"; // Chuyển hướng về danh sách người dùng sau khi cập nhật thành công
    }
}
