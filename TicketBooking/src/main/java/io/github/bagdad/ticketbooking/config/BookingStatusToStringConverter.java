package io.github.bagdad.ticketbooking.config;

import io.github.bagdad.ticketbooking.model.BookingStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@WritingConverter
@Component
public class BookingStatusToStringConverter implements Converter<BookingStatus, String> {
    @Override
    public String convert(BookingStatus source) {
        return source == null ? null : source.name();
    }
}

