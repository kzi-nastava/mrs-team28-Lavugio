package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.user.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChatDTO {
    @PositiveOrZero
    Long userId;

    @NotBlank
    String email;

    public UserChatDTO(Account user){
        this.userId = user.getId();
        this.email = user.getEmail();
    }
}
