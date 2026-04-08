package com.Skoglund.Padel.dto;

import java.math.BigDecimal;

public class CourtDTO {

    private Long id;
    private String name;
    private String surface;
    private boolean indoors;
    private BigDecimal pricePerHourSek;

    public CourtDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSurface() { return surface; }
    public void setSurface(String surface) { this.surface = surface; }

    public boolean isIndoors() { return indoors; }
    public void setIndoors(boolean indoors) { this.indoors = indoors; }

    public BigDecimal getPricePerHourSek() { return pricePerHourSek; }
    public void setPricePerHourSek(BigDecimal pricePerHourSek) { this.pricePerHourSek = pricePerHourSek; }
}