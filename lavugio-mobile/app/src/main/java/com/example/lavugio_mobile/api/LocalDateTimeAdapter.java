package com.example.lavugio_mobile.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Gson adapter for java.time.LocalDateTime.
 * Handles multiple backend formats:
 *   "2026-02-13T21:23:00"
 *   "2026-02-13T21:23:00.785"
 *   "2026-02-13T21:23:00.7859622"
 */
public class LocalDateTimeAdapter extends TypeAdapter<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void write(JsonWriter out, LocalDateTime value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value.format(FORMATTER));
        }
    }

    @Override
    public LocalDateTime read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        String dateStr = in.nextString();
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(dateStr, FORMATTER);
        } catch (DateTimeParseException e) {
            // Try trimming nanoseconds if too many digits
            try {
                // Some backends send 7+ fractional digits; truncate to 9 (nano precision)
                int dotIndex = dateStr.indexOf('.');
                if (dotIndex > 0 && dateStr.length() - dotIndex - 1 > 9) {
                    dateStr = dateStr.substring(0, dotIndex + 10);
                }
                return LocalDateTime.parse(dateStr, FORMATTER);
            } catch (DateTimeParseException e2) {
                return null;
            }
        }
    }
}