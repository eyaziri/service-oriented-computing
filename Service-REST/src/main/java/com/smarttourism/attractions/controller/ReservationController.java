package com.smarttourism.attractions.controller;

import com.smarttourism.attractions.dto.ReservationDTO;
import com.smarttourism.attractions.dto.requests.CreateReservationRequest;
import com.smarttourism.attractions.Entities.Reservation;
import com.smarttourism.attractions.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "API pour la gestion des réservations touristiques")
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @Operation(
        summary = "Créer une nouvelle réservation",
        description = "Crée une réservation pour une attraction touristique avec validation de disponibilité"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Réservation créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou indisponibilité"),
        @ApiResponse(responseCode = "404", description = "Attraction non trouvée"),
        @ApiResponse(responseCode = "409", description = "Réservation déjà existante pour cette date")
    })
    public ResponseEntity<ReservationDTO> createReservation(
            @Valid @RequestBody CreateReservationRequest request) {
        ReservationDTO created = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer une réservation par ID",
        description = "Retourne les détails complets d'une réservation spécifique"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
        @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    public ResponseEntity<ReservationDTO> getReservationById(
            @Parameter(description = "ID de la réservation", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping("/code/{reservationCode}")
    @Operation(
        summary = "Récupérer une réservation par code",
        description = "Retourne les détails d'une réservation à partir de son code unique"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Réservation trouvée"),
        @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    public ResponseEntity<ReservationDTO> getReservationByCode(
            @Parameter(description = "Code de réservation", required = true, example = "RES-A1B2C3D4")
            @PathVariable String reservationCode) {
        return ResponseEntity.ok(reservationService.getReservationByCode(reservationCode));
    }

    @GetMapping("/tourist/{touristId}")
    @Operation(
        summary = "Récupérer les réservations d'un touriste",
        description = "Retourne toutes les réservations d'un touriste spécifique"
    )
    public ResponseEntity<List<ReservationDTO>> getReservationsByTouristId(
            @Parameter(description = "ID du touriste", required = true, example = "tourist_12345")
            @PathVariable String touristId) {
        return ResponseEntity.ok(reservationService.getReservationsByTouristId(touristId));
    }

    @GetMapping("/tourist/{touristId}/paginated")
    @Operation(
        summary = "Récupérer les réservations d'un touriste avec pagination",
        description = "Retourne les réservations d'un touriste avec pagination et tri"
    )
    public ResponseEntity<Page<ReservationDTO>> getReservationsByTouristIdPaginated(
            @Parameter(description = "ID du touriste", required = true)
            @PathVariable String touristId,
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "reservationTime")
            @RequestParam(defaultValue = "reservationTime") String sortBy,
            @Parameter(description = "Ordre de tri", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        return ResponseEntity.ok(reservationService.getReservationsByTouristId(touristId, pageable));
    }

    @GetMapping("/attraction/{attractionId}")
    @Operation(
        summary = "Récupérer les réservations d'une attraction",
        description = "Retourne toutes les réservations pour une attraction spécifique"
    )
    public ResponseEntity<List<ReservationDTO>> getReservationsByAttractionId(
            @Parameter(description = "ID de l'attraction", required = true, example = "1")
            @PathVariable Long attractionId) {
        return ResponseEntity.ok(reservationService.getReservationsByAttractionId(attractionId));
    }

    @GetMapping("/attraction/{attractionId}/paginated")
    @Operation(
        summary = "Récupérer les réservations d'une attraction avec pagination",
        description = "Retourne les réservations d'une attraction avec pagination"
    )
    public ResponseEntity<Page<ReservationDTO>> getReservationsByAttractionIdPaginated(
            @PathVariable Long attractionId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reservationService.getReservationsByAttractionId(attractionId, pageable));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour une réservation",
        description = "Met à jour les informations d'une réservation existante"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Réservation mise à jour"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    public ResponseEntity<ReservationDTO> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody CreateReservationRequest request) {
        // Note: Vous devrez créer une méthode updateReservation dans le service
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, Reservation.ReservationStatus.CONFIRMED));
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Mettre à jour le statut d'une réservation",
        description = "Met à jour le statut d'une réservation existante"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statut mis à jour"),
        @ApiResponse(responseCode = "400", description = "Transition de statut invalide"),
        @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    public ResponseEntity<ReservationDTO> updateReservationStatus(
            @PathVariable Long id,
            @Parameter(description = "Nouveau statut", required = true, example = "CANCELLED")
            @RequestParam Reservation.ReservationStatus status) {
        return ResponseEntity.ok(reservationService.updateReservationStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    @Operation(
        summary = "Annuler une réservation",
        description = "Annule une réservation existante avec une raison optionnelle"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Réservation annulée"),
        @ApiResponse(responseCode = "400", description = "Impossible d'annuler (date passée ou déjà annulée)"),
        @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    public ResponseEntity<ReservationDTO> cancelReservation(
            @PathVariable Long id,
            @Parameter(description = "Raison de l'annulation", example = "Changement de plans")
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(reservationService.cancelReservation(id, 
            reason != null ? reason : "Annulée par l'utilisateur"));
    }

    @PostMapping("/{id}/checkin")
    @Operation(
        summary = "Check-in d'une réservation",
        description = "Effectue le check-in pour une réservation (jour de la visite uniquement)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check-in effectué"),
        @ApiResponse(responseCode = "400", description = "Check-in impossible (mauvais jour ou statut invalide)"),
        @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    public ResponseEntity<ReservationDTO> checkInReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.checkInReservation(id));
    }

    @PostMapping("/{id}/checkout")
    @Operation(
        summary = "Check-out d'une réservation",
        description = "Effectue le check-out pour une réservation (après check-in)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Check-out effectué"),
        @ApiResponse(responseCode = "400", description = "Check-out impossible (check-in requis)"),
        @ApiResponse(responseCode = "404", description = "Réservation non trouvée")
    })
    public ResponseEntity<ReservationDTO> checkOutReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.checkOutReservation(id));
    }

    @GetMapping("/availability/{attractionId}")
    @Operation(
        summary = "Vérifier la disponibilité",
        description = "Vérifie la disponibilité pour une attraction à une date donnée"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilité vérifiée"),
        @ApiResponse(responseCode = "404", description = "Attraction non trouvée")
    })
    public ResponseEntity<Map<String, Object>> checkAvailability(
            @Parameter(description = "ID de l'attraction", required = true, example = "1")
            @PathVariable Long attractionId,
            @Parameter(description = "Date de visite", required = true, example = "2024-12-25")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reservationService.checkAvailability(attractionId, date));
    }

    @GetMapping("/statistics/{attractionId}")
    @Operation(
        summary = "Récupérer les statistiques de réservation",
        description = "Retourne les statistiques des réservations pour une attraction"
    )
    public ResponseEntity<Map<String, Object>> getReservationStatistics(
            @Parameter(description = "ID de l'attraction", required = true, example = "1")
            @PathVariable Long attractionId) {
        return ResponseEntity.ok(reservationService.getReservationStatistics(attractionId));
    }

    @GetMapping("/date-range")
    @Operation(
        summary = "Récupérer les réservations par période",
        description = "Retourne les réservations entre deux dates (pour rapports)"
    )
    public ResponseEntity<List<ReservationDTO>> getReservationsByDateRange(
            @Parameter(description = "Date de début", required = true, example = "2024-12-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "Date de fin", required = true, example = "2024-12-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        // Note: Vous devrez créer cette méthode dans le service et repository
        // return ResponseEntity.ok(reservationService.getReservationsByDateRange(startDate, endDate));
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer définitivement une réservation",
        description = "Supprime définitivement une réservation (uniquement pour l'administration)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Réservation supprimée"),
        @ApiResponse(responseCode = "404", description = "Réservation non trouvée"),
        @ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        // Note: Vous devrez créer une méthode deleteReservation dans le service
        // reservationService.deleteReservation(id);
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/today")
    @Operation(
        summary = "Récupérer les réservations du jour",
        description = "Retourne toutes les réservations pour la date actuelle"
    )
    public ResponseEntity<List<ReservationDTO>> getTodayReservations() {
        LocalDate today = LocalDate.now();
        // Note: Vous devrez créer cette méthode dans le service
        // return ResponseEntity.ok(reservationService.getReservationsByDate(today));
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/upcoming/{touristId}")
    @Operation(
        summary = "Récupérer les réservations à venir d'un touriste",
        description = "Retourne les réservations futures d'un touriste"
    )
    public ResponseEntity<List<ReservationDTO>> getUpcomingReservations(
            @PathVariable String touristId) {
        LocalDate today = LocalDate.now();
        // Note: Vous devrez créer cette méthode dans le service
        // return ResponseEntity.ok(reservationService.getUpcomingReservations(touristId, today));
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}