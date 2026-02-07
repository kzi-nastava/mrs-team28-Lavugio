package com.backend.lavugio.dto.user;

import com.backend.lavugio.model.user.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChatDTO {
    Long userId;
    String email;

    public UserChatDTO(Account user){
        this.userId = user.getId();
        this.email = user.getEmail();
    }
}
