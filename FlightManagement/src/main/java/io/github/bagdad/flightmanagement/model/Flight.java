package io.github.bagdad.flightmanagement.model;


import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Table("flights")
@RequiredArgsConstructor
@Getter
@Setter
public class Flight {

    @Id
    private Long id;

    private String number;

    private String fromCity;

    private String toCity;

    private OffsetDateTime departure;

    private OffsetDateTime arrival;

    private Integer passengerCount;

    private BigDecimal ticketPrice;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public boolean canBook(Integer passengerCount) {
        return this.passengerCount >= passengerCount;
    }

    public void book(Integer passengerCount) {
        this.passengerCount -= passengerCount;
    }

    public void cancelReservation(Integer passengerCount) {
        this.passengerCount += passengerCount;
    }

}
