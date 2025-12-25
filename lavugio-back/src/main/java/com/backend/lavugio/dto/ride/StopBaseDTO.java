package com.backend.lavugio.dto.ride;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StopBaseDTO {
    private int orderIndex;
    private double latitude;
    private double longitude;
}
