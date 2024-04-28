package org.clearsolutions.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.clearsolutions.task.dto.UserCreateRequest;
import org.clearsolutions.task.dto.UserResponse;
import org.clearsolutions.task.dto.UserUpdateRequest;
import org.clearsolutions.task.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Validated
@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "This method is used for user creation.")
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        return userService.createUser(request);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This method is used for receiving all users with pagination.")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This method is used for user update. You can update all fields or just some of them " +
            "or even one field.")
    public UserResponse updateUser(@PathVariable("userId") @Min(1) Long id,
                                   @Valid @RequestBody UserUpdateRequest request) {
        return userService.updateUser(id, request);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This method is used to delete user from the database.")
    public void deleteUser(@PathVariable("userId") @Min(1) Long id) {
        userService.deleteUser(id);
    }

    @GetMapping("/birthdays")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "This method is used for searching users by the birthday range")
    public Page<UserResponse> searchUsersByBirthDate(@RequestParam("from") @Parameter(example = "1997-03-10") LocalDate from,
                                                     @RequestParam("to") @Parameter(example = "2000-01-26") LocalDate to,
                                                     @Parameter(example = "{\"page\":0,\"size\":10,\"sort\":[\"firstName,asc\"]}")
                                                         Pageable pageable) {
        return userService.searchUsersByBirthDate(from, to, pageable);
    }
}
