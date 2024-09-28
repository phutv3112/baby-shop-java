package com.phs.application.controller.shop;

import com.phs.application.config.Contant;
import com.phs.application.entity.Shipper;
import com.phs.application.entity.User;
import com.phs.application.exception.BadRequestException;
import com.phs.application.model.dto.SignInDTO;
import com.phs.application.model.dto.UserDTO;
import com.phs.application.model.mapper.UserMapper;
import com.phs.application.model.request.*;
import com.phs.application.model.response.ResponseOK;
import com.phs.application.security.CustomUserDetails;
import com.phs.application.security.JwtTokenUtil;
import com.phs.application.service.UserService;
import com.phs.application.service.impl.ShipperServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private ShipperServiceImpl shipperServiceImpl;

    @GetMapping("/users")
    public ResponseEntity<Object> getListUsers() {
        List<UserDTO> userDTOS = userService.getListUsers();
        return ResponseEntity.ok(userDTOS);
    }

    @PostMapping("/api/admin/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody CreateUserRequest createUserRequest){
        User user = userService.createUser(createUserRequest);
        return ResponseEntity.ok(UserMapper.toUserDTO(user));
    }

    @PostMapping("/api/register")
    public ResponseEntity<Object> register(@Valid @RequestBody CreateUserRequest createUserRequest, HttpServletResponse response) {
        //Create user
        User user = userService.createUser(createUserRequest);

        //Gen token
        //UserDetails principal = new CustomUserDetails(user);
        CustomUserDetails principal = new CustomUserDetails(user);
        String token = jwtTokenUtil.generateToken(principal);

        //Add token on cookie to login
        Cookie cookie = new Cookie("JWT_TOKEN", token);
        cookie.setMaxAge(Contant.MAX_AGE_COOKIE);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok(UserMapper.toUserDTO(user));
    }

    @PostMapping("/api/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        //Authenticate
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
            ));
            //Gen token
            String token = jwtTokenUtil.generateToken((CustomUserDetails) authentication.getPrincipal());

            System.out.println("token user controller :=========================================== " + token);

            //Add token to cookie to login
            Cookie cookie = new Cookie("JWT_TOKEN", token);
            cookie.setMaxAge(Contant.MAX_AGE_COOKIE);
            cookie.setPath("/");
            response.addCookie(cookie);

            return ResponseEntity.ok(UserMapper.toUserDTO(((CustomUserDetails) authentication.getPrincipal()).getUser()));

        } catch (Exception ex) {
            throw new BadRequestException("Email hoặc mật khẩu không chính xác!");

        }
    }

    @GetMapping("/tai-khoan")
    public String getProfilePage(Model model) {
        return "shop/account";
    }

    @PostMapping("/api/change-password/{userId}")
    public ResponseEntity<ResponseOK> changePassword(@Valid @PathVariable Long userId, @RequestBody ChangePasswordRequest passwordReq) {
        User user = userService.findById(userId);

        if (user == null) {
            ResponseOK response = new ResponseOK("404", "FAIL", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        userService.changePassword(user, passwordReq);
        ResponseOK response = new ResponseOK("200", "OK", "SUCCESS");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/update-profile/{userId}")
    public ResponseEntity<ResponseOK> updateProfile(@PathVariable Long userId, @Valid @RequestBody UpdateProfileRequest profileReq) {
        User user = userService.findById(userId);

        if (user == null) {
            ResponseOK response = new ResponseOK("404", "FAIL", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        user = userService.updateProfile(user, profileReq);
        ResponseOK response = new ResponseOK("200", "OK", "SUCCESS");
        return ResponseEntity.ok(response);
    }
    @PostMapping("/api/shipper/log-in")
    public ResponseEntity<Object> signInShipper(@RequestBody SignInShipperRequest request) {
        SignInDTO userCreden = shipperServiceImpl.signIn(request.getUsername(),request.getPassword());

        if (userCreden==null) {
            ResponseOK response = new ResponseOK("404", "FAIL", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(userCreden);
    }

}
