package io.github.bagdad.flightmanagement.model;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

//public class StatisticsMapper implements RowMapper<FlightStatistics> {
//
//    @Override
//    public FlightStatistics mapRow(ResultSet rs, int rowNum) throws SQLException {
//        FlightStatistics flightStatistics = new FlightStatistics();
//        flightStatistics.setTheMostDepartureCity(rs.getString("booking_count"));
//        flightStatistics.setTheMostVisitingCity(rs.getString("total_passenger_count"));
//        flightStatistics.setTotalRevenue(rs.getBigDecimal("average_passenger_count"));
//        flightStatistics.setTotalFlights(rs.getInt("total_flights"));
//
//        return flightStatistics;
//    }
//
//}