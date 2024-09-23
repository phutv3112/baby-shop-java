package com.phs.application.service;


import com.phs.application.entity.User;
import com.phs.application.model.dto.UserDTO;
import com.phs.application.model.request.ChangePasswordRequest;
import com.phs.application.model.request.CreateUserRequest;
import com.phs.application.model.request.CreateUserRequestDto;
import com.phs.application.model.request.UpdateProfileRequest;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface UserService {
    List<UserDTO> getListUsers();

    Page<User> adminListUserPages(String fullName, String phone, String email, Integer page);

    User createUser(CreateUserRequest createUserRequest);
    User createNewUser(CreateUserRequestDto createUserRequestDto);

    void changePassword(User user, ChangePasswordRequest changePasswordRequest);

    User updateProfile(User user, UpdateProfileRequest updateProfileRequest);
    User findById(Long id);
    void updateUser(Long id, User user);
    void deleteUserById(Long id);
}
