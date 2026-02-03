package com.backend.lavugio.dto.route;

import lombok.*;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewFavoriteRouteDTO {
    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;;
    @Size(min = 2, message = "At least two destinations are required")  
    private List<@Valid FavoriteRouteDestinationDTO> destinations;
}
