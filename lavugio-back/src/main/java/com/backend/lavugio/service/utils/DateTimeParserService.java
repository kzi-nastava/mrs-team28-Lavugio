package com.backend.lavugio.service.utils;

import java.time.LocalDateTime;

public interface DateTimeParserService {
    /**
     * Parsira string u LocalDateTime na početku dana (00:00)
     * @param dateStr datum u formatu dd-MM-yyyy
     * @return LocalDateTime na početku dana ili null ako je dateStr null/empty
     */
    LocalDateTime parseStartOfDay(String dateStr);

    /**
     * Parsira string u LocalDateTime na kraju dana (23:59:59)
     * @param dateStr datum u formatu dd-MM-yyyy
     * @return LocalDateTime na kraju dana ili null ako je dateStr null/empty
     */
    LocalDateTime parseEndOfDay(String dateStr);
}
