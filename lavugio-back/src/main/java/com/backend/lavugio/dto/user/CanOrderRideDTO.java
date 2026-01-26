package com.backend.lavugio.dto.user;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CanOrderRideDTO {
    private IsAccountBlockedDTO block;
    private boolean isInRide;
}
