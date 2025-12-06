package com.smarttourism.notification.service.grpc;

import com.smarttourism.notification.entity.Notification;
import com.smarttourism.notification.service.NotificationService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.format.DateTimeFormatter;

import java.util.List;

@GrpcService
public class NotificationGrpcServiceImpl 
    extends com.smarttourism.notification.NotificationServiceGrpc.NotificationServiceImplBase {

    @Autowired
    private NotificationService notificationService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void sendAlert(com.smarttourism.notification.AlertRequest request,
                          StreamObserver<com.smarttourism.notification.AlertResponse> responseObserver) {
        
        try {
            // 1. Créer l'alerte via le service
            Notification alert = notificationService.createAlert(
                request.getType(),
                request.getLocation(),
                request.getMessage(),
                request.getSeverity()
            );

            // 2. Construire la réponse gRPC
            com.smarttourism.notification.AlertResponse response = 
                com.smarttourism.notification.AlertResponse.newBuilder()
                .setAlertId(alert.getAlertId())
                .setType(alert.getType().toString())
                .setLocation(alert.getLocation())
                .setMessage(alert.getMessage())
                .setSeverity(alert.getSeverity())
                .setTimestamp(alert.getTimestamp().format(FORMATTER))
                .setStatus(alert.getStatus().toString())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                .withDescription("Failed to create alert: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void streamAlerts(com.smarttourism.notification.StreamRequest request,
                             StreamObserver<com.smarttourism.notification.AlertResponse> responseObserver) {
        
        try {
            List<Notification> activeAlerts = notificationService.getAllActiveAlerts();
            
            // Filtrer par localisation si spécifiée
            activeAlerts.stream()
                .filter(alert -> request.getLocation().isEmpty() || 
                        alert.getLocation().equalsIgnoreCase(request.getLocation()))
                .filter(alert -> request.getAlertTypesList().isEmpty() || 
                        request.getAlertTypesList().contains(alert.getType().toString()))
                .forEach(alert -> {
                    com.smarttourism.notification.AlertResponse response = 
                        com.smarttourism.notification.AlertResponse.newBuilder()
                        .setAlertId(alert.getAlertId())
                        .setType(alert.getType().toString())
                        .setLocation(alert.getLocation())
                        .setMessage(alert.getMessage())
                        .setSeverity(alert.getSeverity())
                        .setTimestamp(alert.getTimestamp().format(FORMATTER))
                        .setStatus(alert.getStatus().toString())
                        .build();
                    
                    responseObserver.onNext(response);
                    
                    try {
                        Thread.sleep(500); // Simulation de délai
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                .withDescription("Stream error: " + e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void checkActiveAlerts(com.smarttourism.notification.CheckRequest request,
                                  StreamObserver<com.smarttourism.notification.AlertListResponse> responseObserver) {
        
        try {
            List<Notification> alerts;
            
            if (request.getLocation().isEmpty()) {
                alerts = notificationService.getAllActiveAlerts();
            } else {
                alerts = notificationService.getActiveAlertsByLocation(request.getLocation());
            }

            com.smarttourism.notification.AlertListResponse.Builder responseBuilder = 
                com.smarttourism.notification.AlertListResponse.newBuilder();

            alerts.forEach(alert -> {
                com.smarttourism.notification.AlertResponse grpcAlert = 
                    com.smarttourism.notification.AlertResponse.newBuilder()
                    .setAlertId(alert.getAlertId())
                    .setType(alert.getType().toString())
                    .setLocation(alert.getLocation())
                    .setMessage(alert.getMessage())
                    .setSeverity(alert.getSeverity())
                    .setTimestamp(alert.getTimestamp().format(FORMATTER))
                    .setStatus(alert.getStatus().toString())
                    .build();
                
                responseBuilder.addAlerts(grpcAlert);
            });

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            responseObserver.onError(io.grpc.Status.INTERNAL
                .withDescription("Failed to check alerts: " + e.getMessage())
                .asRuntimeException());
        }
    }
}