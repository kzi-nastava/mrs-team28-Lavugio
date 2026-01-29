package com.backend.lavugio.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountUpdateDTO {
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Surname cannot be blank")
    private String surname;
    @Pattern(regexp = "^(06\\d{5,9}|\\+381\\d{6,10})$", message = "Phone number must start with '06' followed by 5 to 9 digits or with '+381' followed by 6 to 10 digits")
    private String phoneNumber;
    @NotBlank(message = "Address cannot be blank")  
    private String address;
}