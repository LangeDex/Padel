package com.Skoglund.Padel.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public class BookingPatchDTO {

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer numberOfPlayers;
    private Long courtId;

    public BookingPatchDTO() {}

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getNumberOfPlayers() { return numberOfPlayers; }
    public void setNumberOfPlayers(Integer numberOfPlayers) { this.numberOfPlayers = numberOfPlayers; }

    public Long getCourtId() { return courtId; }
    public void setCourtId(Long courtId) { this.courtId = courtId; }
}