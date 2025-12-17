package io.github.bagdad.flightmanagement.helper;

import io.github.bagdad.flightmanagement.model.Flight;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class CSVHelper {

    private static final String[] HEADERS = {"number", "from_city", "to_city", "departure", "arrival", "passenger_count", "ticket_price", "created_at", "updated_at"};

    public static ByteArrayInputStream writeToCSC(List<Flight> flights) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(outputStream), CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .build());

        for (Flight flight: flights) {
            csvPrinter.printRecord(
                    flight.getNumber(),
                    flight.getFromCity(),
                    flight.getToCity(),
                    flight.getDeparture(),
                    flight.getArrival(),
                    flight.getPassengerCount(),
                    flight.getTicketPrice(),
                    flight.getCreatedAt(),
                    flight.getUpdatedAt()
            );
        }

        csvPrinter.flush();

        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    public static List<Flight> loadFromCSV(InputStream file) throws IOException {
        List<Flight> flights = new ArrayList<>();

        try (InputStreamReader reader = new InputStreamReader(file);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder()
                     .setHeader(HEADERS)
                     .setSkipHeaderRecord(true)
                     .setTrim(true)
                     .build())
        ) {

            for (CSVRecord record : csvParser) {
                Flight flight = new Flight();

                try {
                    flight.setNumber(record.get("number"));
                    flight.setFromCity(record.get("from_city"));
                    flight.setToCity(record.get("to_city"));
                    flight.setDeparture(OffsetDateTime.parse(record.get("departure"), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    flight.setArrival(OffsetDateTime.parse(record.get("arrival"), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
                    flight.setPassengerCount(Integer.parseInt(record.get("passenger_count")));
                    flight.setTicketPrice(new BigDecimal(record.get("ticket_price")));
                    flight.setCreatedAt(LocalDateTime.parse(record.get("created_at")));
                    flight.setUpdatedAt(LocalDateTime.parse(record.get("updated_at")));

                    flights.add(flight);
                }
                catch (Exception e) {
                    System.err.println("Непредвиденное исключение: " + e.getMessage());
                }
            }
        }

        return flights;
    }
}