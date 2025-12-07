package com.smarttourism.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertDTO {
    private String alertId;
    private String type;
    private String location;
    private String message;
    private int severity;
    private String timestamp;
    private String status;
}