package com.Skoglund.Padel.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "courts")
public class Court {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surface;

    private boolean indoors;

    @Column(nullable = false)
    private BigDecimal pricePerHourSek;

    public Court() {}

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