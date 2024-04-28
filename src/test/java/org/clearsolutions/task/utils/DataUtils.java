package org.clearsolutions.task.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.clearsolutions.task.dto.UserCreateRequest;
import org.clearsolutions.task.dto.UserResponse;
import org.clearsolutions.task.dto.UserUpdateRequest;
import org.clearsolutions.task.entity.User;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataUtils {

    public static UserCreateRequest createUserCreateRequest(){
        return UserCreateRequest.builder()
                .firstName("Oleksii")
                .lastName("Ivanchenko")
                .email("oleksii.ivanchenko@gmail.com")
                .birthDate(LocalDate.parse("2001-04-25"))
                .address("Ukraine, Kyiv, Shevchenko str. 5")
                .phoneNumber("+380 93 123 4567")
                .build();
    }

    public static UserResponse toUserResponse(UserCreateRequest request){
        return UserResponse.builder()
                .id(1L)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .phoneNumber(createUserCreateRequest().getPhoneNumber())
                .build();
    }

    public static UserResponse toUserResponse(UserUpdateRequest request){
        return UserResponse.builder()
                .id(1L)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .phoneNumber(createUserCreateRequest().getPhoneNumber())
                .build();
    }


    public static List<UserResponse> createUserResponseList() {
        return List.of(
                new UserResponse(1L, "Oleksii", "Ivanyuk", "o.ivanyuk@gmail.com",
                        LocalDate.parse("1989-06-27"), "Kyiv, Urkaine", "+380 93 123 4567"),
                new UserResponse(2L, "Ivan", "Piddubko", "dub123@gmail.com",
                        LocalDate.parse("1991-02-21"), "Poltava, Urkaine", "+380 93 123 6565"),
                new UserResponse(3L, "Oksana", "Stefanchuk", "oksana@gmail.com",
                        LocalDate.parse("2000-02-01"), "Lviv, Urkaine", "+380 50 123 6565"),
                new UserResponse(4L, "Iryna", "Stecko", "stec@gmail.com",
                        LocalDate.parse("2005-11-15"), "Nighyn, Urkaine", "+380 50 123 6565")
                );
    }

    public static List<User> createUserList() {
        return List.of(
                new User(1L, "Oleksii", "Ivanyuk", "o.ivanyuk@gmail.com",
                        LocalDate.parse("1989-06-27"), "Kyiv, Urkaine", "+380 93 123 4567"),
                new User(2L, "Ivan", "Piddubko", "dub123@gmail.com",
                        LocalDate.parse("1991-02-21"), "Poltava, Urkaine", "+380 93 123 6565"),
                new User(3L, "Oksana", "Stefanchuk", "oksana@gmail.com",
                        LocalDate.parse("2000-02-01"), "Lviv, Urkaine", "+380 50 123 6565"),
                new User(4L, "Iryna", "Stecko", "stec@gmail.com",
                        LocalDate.parse("2005-11-15"), "Nighyn, Urkaine", "+380 50 123 6565")
        );
    }

    public static User toUser(UserCreateRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .phoneNumber(createUserCreateRequest().getPhoneNumber())
                .build();
    }

    public static User toUser(UserUpdateRequest request) {
        return User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .phoneNumber(createUserCreateRequest().getPhoneNumber())
                .build();
    }

    public static UserResponse toUserResponse(User user){
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .phoneNumber(createUserCreateRequest().getPhoneNumber())
                .build();
    }
}
