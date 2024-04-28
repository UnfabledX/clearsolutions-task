package org.clearsolutions.task.service;

import org.clearsolutions.task.dto.UserCreateRequest;
import org.clearsolutions.task.dto.UserResponse;
import org.clearsolutions.task.dto.UserUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface UserService {
    UserResponse createUser(UserCreateRequest request);

    Page<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    Page<UserResponse> searchUsersByBirthDate(LocalDate from, LocalDate to, Pageable pageable);
}
