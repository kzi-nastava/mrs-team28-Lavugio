package com.backend.lavugio.dto.user;

import jakarta.persistence.Column;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String address;
    private String profilePhotoPath;
    private boolean blocked;
    private String blockReason;
    private boolean emailVerified;
}
