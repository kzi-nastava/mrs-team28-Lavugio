package com.backend.lavugio.service.utils.impl;

import com.backend.lavugio.service.utils.DateTimeParserService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class DateTimeParserServiceImpl implements DateTimeParserService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public LocalDateTime parseStartOfDay(String dateStr) {
        if (dateStr == null || dateStr.isBlank()){
            LocalDate date = LocalDate.MIN;
            return date.atStartOfDay(); // 00:00
        };
        try {
            LocalDate date = LocalDate.parse(dateStr, FORMATTER);
            return date.atStartOfDay(); // 00:00
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected dd-MM-yyyy: " + dateStr);
        }
    }

    @Override
    public LocalDateTime parseEndOfDay(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return LocalDateTime.MAX;
        try {
            LocalDate date = LocalDate.parse(dateStr, FORMATTER);
            return date.atTime(23, 59, 59); // 23:59:59
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected dd-MM-yyyy: " + dateStr);
        }
    }
}
