package com.smart_tourism.smart_tourism.soap.model.request;

import jakarta.xml.bind.annotation.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement(name = "GetHistoricalDescriptionRequest", namespace = "http://smarttourism.com/soap")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"monumentId"})
public class GetHistoricalDescriptionRequest {

    @XmlElement(namespace = "http://smarttourism.com/soap", required = true)
    private String monumentId;
}