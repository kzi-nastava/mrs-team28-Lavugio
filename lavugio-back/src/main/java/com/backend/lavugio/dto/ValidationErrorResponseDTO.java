package com.backend.lavugio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ValidationErrorResponseDTO {
    private int status;
    private String message;
    private String error;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> fieldErrors;
}
