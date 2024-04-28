package org.clearsolutions.task.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.clearsolutions.task.dto.UserCreateRequest;
import org.clearsolutions.task.dto.UserResponse;
import org.clearsolutions.task.dto.UserUpdateRequest;
import org.clearsolutions.task.entity.User;
import org.clearsolutions.task.exception.YoungAgeException;
import org.clearsolutions.task.mapper.UserMapper;
import org.clearsolutions.task.repository.UserRepository;
import org.clearsolutions.task.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;

@Setter
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Value("${user.age}")
    private Integer ageCondition;

    @Override
    @Transactional
    public UserResponse createUser(UserCreateRequest request) {
        checkIfPersonIsAdultEnough(request);
        User savedUser = userRepository.save(mapper.toEntity(request));
        return mapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(mapper::toResponse);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User existingUser = userRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("User with id='%d' can not be found".formatted(id)));
        mapper.updateUser(existingUser, request);
        return mapper.toResponse(existingUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("User with id='%d' can not be found".formatted(id));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsersByBirthDate(LocalDate from, LocalDate to, Pageable pageable) {
        if (from.isBefore(to)) {
            return userRepository.getUsersByBirthDateBetween(from, to, pageable).map(mapper::toResponse);
        } else {
            throw new IllegalArgumentException("Date `to`-'%s' is before date `from`-'%s'.".formatted(to, from));
        }
    }

    private void checkIfPersonIsAdultEnough(UserCreateRequest request) {
        LocalDate birthday = request.getBirthDate();
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthday, currentDate).getYears();
        if (age < ageCondition) throw new YoungAgeException(
                "You are too young to register. Your birthday is at '%s'".formatted(birthday), HttpStatus.BAD_REQUEST);
    }
}
