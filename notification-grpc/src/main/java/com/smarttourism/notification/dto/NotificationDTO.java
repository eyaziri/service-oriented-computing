package com.smarttourism.notification.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private String alertId;
    private String type;
    private String location;
    private String message;
    private Integer severity;
    private String status;
    private LocalDateTime timestamp;
    private LocalDateTime resolvedAt;
}