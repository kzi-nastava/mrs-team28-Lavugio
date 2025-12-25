package com.backend.lavugio.dto.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    @NotEmpty
    private String email;

    @Size(min = 6)
    @NotEmpty
    private String password;

    @NotEmpty
    private String name;

    @NotEmpty
    private String lastName;

    private String phoneNumber;
    private String profilePhotoPath;
}