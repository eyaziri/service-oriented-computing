package com.smart_tourism.smart_tourism.soap.model;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "Monument")
@XmlAccessorType(XmlAccessType.FIELD)

public class Monument {
    @XmlElement(name = "id")
    private String monumentId;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "city")
    private String city;

    @XmlElement(name = "yearBuilt")
    private Integer yearBuilt;

    @XmlElement(name = "architecturalStyle")
    private String architecturalStyle;

    @XmlElement(name = "unescoHeritage")
    private Boolean unescoHeritage;

    @XmlElement(name = "historicalPeriod")
    private String historicalPeriod;

    public Monument(String m001, String muséeNationalDuBardo, String tunis, int i, String mauresque, boolean b, String ottoman) {
    }

    public String getMonumentId() {
        return monumentId;
    }

    public String getName() {
        return name;
    }

    public String getHistoricalPeriod() {
        return historicalPeriod;
    }

    public Boolean getUnescoHeritage() {
        return unescoHeritage;
    }

    public String getArchitecturalStyle() {
        return architecturalStyle;
    }

    public Integer getYearBuilt() {
        return yearBuilt;
    }

    public String getCity() {
        return city;
    }

    public Monument(String monumentId, String historicalPeriod, Boolean unescoHeritage, String architecturalStyle, Integer yearBuilt, String city, String name) {
        this.monumentId = monumentId;
        this.historicalPeriod = historicalPeriod;
        this.unescoHeritage = unescoHeritage;
        this.architecturalStyle = architecturalStyle;
        this.yearBuilt = yearBuilt;
        this.city = city;
        this.name = name;
    }

    public void setMonumentId(String monumentId) {
        this.monumentId = monumentId;
    }

    public void setHistoricalPeriod(String historicalPeriod) {
        this.historicalPeriod = historicalPeriod;
    }

    public void setUnescoHeritage(Boolean unescoHeritage) {
        this.unescoHeritage = unescoHeritage;
    }

    public void setArchitecturalStyle(String architecturalStyle) {
        this.architecturalStyle = architecturalStyle;
    }

    public void setYearBuilt(Integer yearBuilt) {
        this.yearBuilt = yearBuilt;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setName(String name) {
        this.name = name;
    }
}
