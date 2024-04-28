package org.clearsolutions.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class UserCreateRequest {

    @Schema(example = "Oleksii")
    @Size(min = 2, message = "The first name must be more than 2 letters.")
    @NotNull
    private String firstName;

    @Schema(example = "Ivanchenko")
    @Size(min = 2, message = "The first name must be more than 2 letters.")
    @NotNull
    private String lastName;

    @Schema(example = "oleksii.ivanchenko@gmail.com")
    @Email(message = "Wrong email format.")
    @NotNull
    private String email;

    @Schema(example = "2001-04-25")
    @Past(message = "The date must be in the past.")
    @NotNull
    private LocalDate birthDate;

    @Schema(example = "Ukraine, Kyiv, Shevchenko str. 5")
    @Size(max = 500, message = "The address is too long.")
    private String address;

    @Schema(example = "+380 93 123 4567")
    @Pattern(regexp = "^\\+[0-9]{3}\\s?[0-9]{2}\\s?[0-9]{3}\\s?[0-9]{4}$|",
            message = "Invalid phone number. " +
                    "Valid format is +380 93 123 4567 or without spaces +380931234567")
    private String phoneNumber;
}
