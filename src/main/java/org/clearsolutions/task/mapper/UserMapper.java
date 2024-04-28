package org.clearsolutions.task.mapper;

import org.clearsolutions.task.dto.UserCreateRequest;
import org.clearsolutions.task.dto.UserResponse;
import org.clearsolutions.task.dto.UserUpdateRequest;
import org.clearsolutions.task.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {


    User toEntity(UserCreateRequest request);

    UserResponse toResponse(User savedUser);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUser(@MappingTarget User existingUser, UserUpdateRequest request);
}
