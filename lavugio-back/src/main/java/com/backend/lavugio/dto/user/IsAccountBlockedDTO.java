package com.backend.lavugio.dto.user;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IsAccountBlockedDTO {
    private boolean isBlocked;
    private String reason;
}
