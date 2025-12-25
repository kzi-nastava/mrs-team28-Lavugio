package com.backend.lavugio.dto;

import com.backend.lavugio.model.user.RegularUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PassengerTableRowDTO {
    private Long id;
    private String name;
    private ImageDTO profilePicture;

    public PassengerTableRowDTO(RegularUser user){
        this.id = user.getId();
        this.name = user.getName() + " " + user.getLastName();
    }
}
