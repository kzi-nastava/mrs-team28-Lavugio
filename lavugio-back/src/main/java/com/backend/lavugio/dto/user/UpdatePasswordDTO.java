package com.backend.lavugio.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDTO {
    @NotBlank(message = "Old password cannot be blank")
    private String oldPassword;
    @Size(min = 6, message = "New password must be at least 6 characters long")
    private String newPassword;
}
