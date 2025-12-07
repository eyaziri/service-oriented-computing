package com.smarttourism.attractions.Entities;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Double latitude;
    private Double longitude;
    private String address;
    private String postalCode;
    private String city;
    private String country;
    
    public String getFullAddress() {
        return String.format("%s, %s %s, %s", 
            address, postalCode, city, country);
    }
}