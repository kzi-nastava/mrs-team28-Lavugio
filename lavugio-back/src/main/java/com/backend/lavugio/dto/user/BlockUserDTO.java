package com.backend.lavugio.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BlockUserDTO {
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Reason cannot be blank")
    @Size(max = 500, message = "Reason cannot exceed 500 characters")
    private String reason;
}
