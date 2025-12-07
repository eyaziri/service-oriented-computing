package com.smarttourism.notification.service.grpc;

import com.smarttourism.notification.dto.CreateNotificationRequest;
import com.smarttourism.notification.entity.Notification;
import com.smarttourism.notification.service.NotificationService;

// FIXED: Correct imports for generated protobuf classes
import com.smarttourism.notification.AlertRequest;
import com.smarttourism.notification.AlertResponse;
import com.smarttourism.notification.StreamRequest;
import com.smarttourism.notification.CheckRequest;
import com.smarttourism.notification.AlertListResponse;
import com.smarttourism.notification.NotificationServiceGrpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@GrpcService
@RequiredArgsConstructor
public class NotificationGrpcServiceImpl extends NotificationServiceGrpc.NotificationServiceImplBase {
    
    private final NotificationService notificationService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    @Override
    public void sendAlert(AlertRequest request, StreamObserver<AlertResponse> responseObserver) {
        log.info("gRPC: Sending alert - type: {}, location: {}", request.getType(), request.getLocation());
        
        try {
            // Convertir la requête gRPC en DTO
            CreateNotificationRequest dtoRequest = new CreateNotificationRequest();
            dtoRequest.setType(request.getType());
            dtoRequest.setLocation(request.getLocation());
            dtoRequest.setMessage(request.getMessage());
            dtoRequest.setSeverity(request.getSeverity());
            
            // Créer l'alerte via le service
            Notification notification = notificationService.createAlert(dtoRequest);
            
            // Construire la réponse gRPC
            AlertResponse response = AlertResponse.newBuilder()
                .setAlertId(notification.getAlertId())
                .setType(notification.getType().name())
                .setLocation(notification.getLocation())
                .setMessage(notification.getMessage())
                .setSeverity(notification.getSeverity())
                .setTimestamp(notification.getTimestamp().format(formatter))
                .setStatus(notification.getStatus().name())
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
            log.info("gRPC: Alert sent successfully - ID: {}", notification.getAlertId());
            
        } catch (Exception e) {
            log.error("gRPC: Error sending alert", e);
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void streamAlerts(StreamRequest request, StreamObserver<AlertResponse> responseObserver) {
        log.info("gRPC: Streaming alerts for location: {}", request.getLocation());
        
        try {
            List<Notification> activeAlerts;
            
            if (request.getLocation().isEmpty()) {
                activeAlerts = notificationService.getActiveAlerts();
            } else {
                activeAlerts = notificationService.getAlertsByLocation(request.getLocation());
            }
            
            // Filtrer par types si spécifiés
            if (!request.getAlertTypesList().isEmpty()) {
                activeAlerts = activeAlerts.stream()
                    .filter(alert -> request.getAlertTypesList().contains(alert.getType().name()))
                    .toList();
            }
            
            // Stream chaque alerte
            for (Notification alert : activeAlerts) {
                AlertResponse response = AlertResponse.newBuilder()
                    .setAlertId(alert.getAlertId())
                    .setType(alert.getType().name())
                    .setLocation(alert.getLocation())
                    .setMessage(alert.getMessage())
                    .setSeverity(alert.getSeverity())
                    .setTimestamp(alert.getTimestamp().format(formatter))
                    .setStatus(alert.getStatus().name())
                    .build();
                
                responseObserver.onNext(response);
                
                // Simuler un délai pour le streaming
                Thread.sleep(500);
            }
            
            responseObserver.onCompleted();
            log.info("gRPC: Stream completed - {} alerts sent", activeAlerts.size());
            
        } catch (Exception e) {
            log.error("gRPC: Error in streaming alerts", e);
            responseObserver.onError(e);
        }
    }
    
    @Override
    public void checkActiveAlerts(CheckRequest request, StreamObserver<AlertListResponse> responseObserver) {
        log.info("gRPC: Checking active alerts for location: {}", request.getLocation());
        
        try {
            List<Notification> activeAlerts;
            
            if (request.getLocation().isEmpty()) {
                activeAlerts = notificationService.getActiveAlerts();
            } else {
                activeAlerts = notificationService.getAlertsByLocation(request.getLocation());
            }
            
            // Construire la réponse
            AlertListResponse.Builder responseBuilder = AlertListResponse.newBuilder();
            
            for (Notification alert : activeAlerts) {
                AlertResponse alertResponse = AlertResponse.newBuilder()
                    .setAlertId(alert.getAlertId())
                    .setType(alert.getType().name())
                    .setLocation(alert.getLocation())
                    .setMessage(alert.getMessage())
                    .setSeverity(alert.getSeverity())
                    .setTimestamp(alert.getTimestamp().format(formatter))
                    .setStatus(alert.getStatus().name())
                    .build();
                
                responseBuilder.addAlerts(alertResponse);
            }
            
            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
            log.info("gRPC: Active alerts checked - found {} alerts", activeAlerts.size());
            
        } catch (Exception e) {
            log.error("gRPC: Error checking active alerts", e);
            responseObserver.onError(e);
        }
    }
}