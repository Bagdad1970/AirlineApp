package io.github.bagdad.flightmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FlightStatistics {

    private String theMostDepartureCity;

    private String theMostVisitingCity;

    private Integer totalFlights;

}
