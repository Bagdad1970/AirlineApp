package io.github.bagdad.ticketbooking.config;

import io.github.bagdad.ticketbooking.model.BookingStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@ReadingConverter
@Component
public class StringToBookingStatusConverter implements Converter<String, BookingStatus> {
    @Override
    public BookingStatus convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        try {
            return BookingStatus.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cannot convert string '" + source + "' to BookingStatus", e);
        }
    }
}