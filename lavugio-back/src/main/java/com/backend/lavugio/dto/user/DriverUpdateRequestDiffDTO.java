package com.backend.lavugio.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverUpdateRequestDiffDTO {
    private Long requestId;

    private DriverUpdateRequestDTO oldData;
    private DriverUpdateRequestDTO newData;

    private String email;
}
