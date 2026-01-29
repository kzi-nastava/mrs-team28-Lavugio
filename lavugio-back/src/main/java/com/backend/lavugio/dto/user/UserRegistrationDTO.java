package com.backend.lavugio.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email is required")
    private String email;

    private String password;

    @NotEmpty(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotEmpty(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @NotEmpty(message = "Phone number is required")
    @Pattern(regexp = "^(\\+381|0)[0-9\\s\\-()]{6,14}$", message = "Phone number must be valid Serbian format (+381 or 0)")
    private String phoneNumber;

    private String profilePhotoPath;

    @NotEmpty(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    private String address;
}