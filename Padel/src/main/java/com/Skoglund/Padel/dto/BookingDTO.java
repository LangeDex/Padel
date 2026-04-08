package com.Skoglund.Padel.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class BookingDTO {

    private Long id;
    private Long customerId;
    private String customerName;
    private Long courtId;
    private String courtName;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int numberOfPlayers;
    private BigDecimal totalPriceSek;
    private BigDecimal totalPriceEur;

    public BookingDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public Long getCourtId() { return courtId; }
    public void setCourtId(Long courtId) { this.courtId = courtId; }

    public String getCourtName() { return courtName; }
    public void setCourtName(String courtName) { this.courtName = courtName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public int getNumberOfPlayers() { return numberOfPlayers; }
    public void setNumberOfPlayers(int numberOfPlayers) { this.numberOfPlayers = numberOfPlayers; }

    public BigDecimal getTotalPriceSek() { return totalPriceSek; }
    public void setTotalPriceSek(BigDecimal totalPriceSek) { this.totalPriceSek = totalPriceSek; }

    public BigDecimal getTotalPriceEur() { return totalPriceEur; }
    public void setTotalPriceEur(BigDecimal totalPriceEur) { this.totalPriceEur = totalPriceEur; }
}