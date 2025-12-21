package com.backend.lavugio.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountProfileDTO {
        private Long id;
        private String name;
        private String lastName;
        private String email;
        private String profilePhotoPath;
        private String accountType;
}
