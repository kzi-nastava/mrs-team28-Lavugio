package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.user.RegularUser;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassengerTableRowDTO {
    @NotNull
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String passengerIconName;

    public PassengerTableRowDTO(RegularUser user){
        this.id = user.getId();
        this.name = user.getName() + " " + user.getLastName();
        String[] parts = user.getProfilePhotoPath().split("[/\\\\]");
        this.passengerIconName = parts[parts.length - 1];
    }
}
