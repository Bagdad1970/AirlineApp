package io.github.bagdad.ticketbooking.helper;

import io.github.bagdad.ticketbooking.model.Booking;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    private static final String[] HEADERS = {"flight_id", "passenger_count", "created_at", "updated_at"};


    public static ByteArrayInputStream writeCsv(List<Booking> bookings) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(outputStream), CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .build());

        for (Booking booking: bookings) {
            csvPrinter.printRecord(
                    booking.getFlightId(),
                    booking.getPassengerCount(),
                    booking.getCreatedAt(),
                    booking.getUpdatedAt()
            );
        }

        csvPrinter.flush();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static List<Booking> loadFromCSV(InputStream file) throws IOException {
        List<Booking> bookings = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(file);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setHeader(HEADERS)
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .build())
        ) {

            for (CSVRecord record : csvParser) {
                Booking booking = new Booking();

                try {
                    booking.setFlightId(Long.parseLong(record.get("number")));
                    booking.setPassengerCount(Integer.parseInt(record.get("from_city")));
                    booking.setCreatedAt(LocalDateTime.parse(record.get("created_at")));
                    booking.setUpdatedAt(LocalDateTime.parse(record.get("updated_at")));

                    bookings.add(booking);
                }
                catch (Exception e) {
                    System.err.println("Непредвиденное исключение: " + e.getMessage());
                }
            }
        }

        return bookings;
    }
}