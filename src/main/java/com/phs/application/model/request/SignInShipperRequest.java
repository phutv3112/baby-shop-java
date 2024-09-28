package com.phs.application.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SignInShipperRequest {
    @NotBlank(message = "Tên tài khoản trống")
    private String username;
    @NotBlank(message = "Mật khẩu trống")
    @Size(min = 6, max = 20,message = "Mật khẩu phải chứa từ 6-20 ký tự")
    private String password;
}
