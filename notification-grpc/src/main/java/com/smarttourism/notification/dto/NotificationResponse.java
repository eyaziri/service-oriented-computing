package com.smarttourism.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private String alertId;
    private String type;
    private String location;
    private String message;
    private Integer severity;
    private String status;
    private String timestamp;
    private String resolvedAt; // Nouveau champ

}