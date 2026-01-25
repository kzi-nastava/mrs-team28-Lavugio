package com.backend.lavugio.dto.user;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BlockUserDTO {
    private String email;
    private String reason;
}
