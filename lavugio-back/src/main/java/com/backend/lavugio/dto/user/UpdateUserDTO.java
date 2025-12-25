package com.backend.lavugio.dto.user;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserDTO {
    private String name;
    private String lastName;
    private String phoneNumber;
    private String profilePhotoPath;
}