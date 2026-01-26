package com.backend.lavugio.dto.user;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateDTO {
    private String name;
    private String surname;
    private String phoneNumber;
    private String address;
}