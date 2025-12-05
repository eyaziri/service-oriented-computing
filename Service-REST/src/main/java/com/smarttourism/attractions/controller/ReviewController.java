package com.smarttourism.attractions.controller;

import com.smarttourism.attractions.dto.ReviewDTO;
import com.smarttourism.attractions.dto.requests.CreateReviewRequest;
import com.smarttourism.attractions.service.ReviewService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Avis", description = "API pour la gestion des avis et évaluations touristiques")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(
        summary = "Créer un nouvel avis",
        description = "Ajoute un nouvel avis pour une attraction touristique. Peut être lié à une réservation pour vérification."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Avis créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou note hors limites"),
        @ApiResponse(responseCode = "404", description = "Attraction ou réservation non trouvée"),
        @ApiResponse(responseCode = "409", description = "Avis déjà existant pour cette attraction")
    })
    public ResponseEntity<ReviewDTO> createReview(
            @Valid @RequestBody CreateReviewRequest request) {
        ReviewDTO created = reviewService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Récupérer un avis par ID",
        description = "Retourne les détails complets d'un avis spécifique"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avis trouvé"),
        @ApiResponse(responseCode = "404", description = "Avis non trouvé")
    })
    public ResponseEntity<ReviewDTO> getReviewById(
            @Parameter(description = "ID de l'avis", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(reviewService.getReviewById(id));
    }

    @GetMapping("/attraction/{attractionId}")
    @Operation(
        summary = "Récupérer les avis d'une attraction",
        description = "Retourne tous les avis pour une attraction spécifique"
    )
    public ResponseEntity<List<ReviewDTO>> getReviewsByAttractionId(
            @Parameter(description = "ID de l'attraction", required = true, example = "1")
            @PathVariable Long attractionId) {
        return ResponseEntity.ok(reviewService.getReviewsByAttractionId(attractionId));
    }

    @GetMapping("/attraction/{attractionId}/paginated")
    @Operation(
        summary = "Récupérer les avis d'une attraction avec pagination",
        description = "Retourne les avis d'une attraction avec pagination"
    )
    public ResponseEntity<Page<ReviewDTO>> getReviewsByAttractionIdPaginated(
            @PathVariable Long attractionId,
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reviewService.getReviewsByAttractionId(attractionId, pageable));
    }

    @GetMapping("/attraction/{attractionId}/sorted")
    @Operation(
        summary = "Récupérer les avis d'une attraction triés",
        description = "Retourne les avis triés par note, date ou utilité"
    )
    public ResponseEntity<Page<ReviewDTO>> getReviewsByAttractionIdSorted(
            @PathVariable Long attractionId,
            @Parameter(description = "Critère de tri: 'date', 'rating', 'helpful'", example = "rating")
            @RequestParam(defaultValue = "date") String sortBy,
            @Parameter(description = "Numéro de page (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Ordre de tri", example = "desc")
            @RequestParam(defaultValue = "desc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "reviewDate"));
        
        return ResponseEntity.ok(reviewService.getReviewsByAttractionIdSorted(attractionId, sortBy, pageable));
    }

    @GetMapping("/tourist/{touristId}")
    @Operation(
        summary = "Récupérer les avis d'un touriste",
        description = "Retourne tous les avis postés par un touriste spécifique"
    )
    public ResponseEntity<List<ReviewDTO>> getReviewsByTouristId(
            @Parameter(description = "ID du touriste", required = true, example = "tourist_12345")
            @PathVariable String touristId) {
        return ResponseEntity.ok(reviewService.getReviewsByTouristId(touristId));
    }

    @GetMapping("/tourist/{touristId}/paginated")
    @Operation(
        summary = "Récupérer les avis d'un touriste avec pagination",
        description = "Retourne les avis d'un touriste avec pagination"
    )
    public ResponseEntity<Page<ReviewDTO>> getReviewsByTouristIdPaginated(
            @PathVariable String touristId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(reviewService.getReviewsByTouristId(touristId, pageable));
    }

    @GetMapping("/attraction/{attractionId}/verified")
    @Operation(
        summary = "Récupérer les avis vérifiés d'une attraction",
        description = "Retourne uniquement les avis vérifiés (avec réservation complétée)"
    )
    public ResponseEntity<List<ReviewDTO>> getVerifiedReviewsByAttractionId(
            @PathVariable Long attractionId) {
        return ResponseEntity.ok(reviewService.getVerifiedReviewsByAttractionId(attractionId));
    }

    @GetMapping("/attraction/{attractionId}/rating/{rating}")
    @Operation(
        summary = "Récupérer les avis par note",
        description = "Retourne les avis d'une attraction avec une note spécifique (1-5)"
    )
    public ResponseEntity<List<ReviewDTO>> getReviewsByRating(
            @PathVariable Long attractionId,
            @Parameter(description = "Note (1-5)", required = true, example = "5")
            @PathVariable Integer rating) {
        
        if (rating < 1 || rating > 5) {
            return ResponseEntity.badRequest().build();
        }
        
        // Note: Vous devrez créer cette méthode dans le service
        // return ResponseEntity.ok(reviewService.getReviewsByAttractionIdAndRating(attractionId, rating));
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Mettre à jour un avis",
        description = "Met à jour un avis existant (délai de 24h maximum)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avis mis à jour"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou délai dépassé"),
        @ApiResponse(responseCode = "404", description = "Avis non trouvé"),
        @ApiResponse(responseCode = "403", description = "Non autorisé (avis d'un autre utilisateur)")
    })
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long id,
            @Valid @RequestBody CreateReviewRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Supprimer un avis",
        description = "Supprime un avis existant (uniquement par l'auteur)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Avis supprimé"),
        @ApiResponse(responseCode = "404", description = "Avis non trouvé"),
        @ApiResponse(responseCode = "403", description = "Non autorisé")
    })
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID de l'avis", required = true)
            @PathVariable Long id,
            @Parameter(description = "ID du touriste (pour vérification)", required = true)
            @RequestParam String touristId) {
        reviewService.deleteReview(id, touristId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/helpful")
    @Operation(
        summary = "Marquer un avis comme utile",
        description = "Incrémente le compteur d'utilité d'un avis"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avis marqué comme utile"),
        @ApiResponse(responseCode = "404", description = "Avis non trouvé")
    })
    public ResponseEntity<ReviewDTO> markReviewAsHelpful(
            @Parameter(description = "ID de l'avis", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(reviewService.markReviewAsHelpful(id));
    }

    @PostMapping("/{id}/reply")
    @Operation(
        summary = "Ajouter une réponse à un avis",
        description = "Ajoute une réponse (par le gestionnaire de l'attraction) à un avis"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Réponse ajoutée"),
        @ApiResponse(responseCode = "404", description = "Avis non trouvé")
    })
    public ResponseEntity<ReviewDTO> addReplyToReview(
            @Parameter(description = "ID de l'avis", required = true)
            @PathVariable Long id,
            @Parameter(description = "Contenu de la réponse", required = true)
            @RequestParam String reply) {
        return ResponseEntity.ok(reviewService.addReplyToReview(id, reply));
    }

    @GetMapping("/statistics/{attractionId}")
    @Operation(
        summary = "Récupérer les statistiques d'avis",
        description = "Retourne les statistiques détaillées des avis pour une attraction"
    )
    public ResponseEntity<Map<String, Object>> getReviewStatistics(
            @Parameter(description = "ID de l'attraction", required = true, example = "1")
            @PathVariable Long attractionId) {
        return ResponseEntity.ok(reviewService.getReviewStatistics(attractionId));
    }

    @GetMapping("/summary/{attractionId}")
    @Operation(
        summary = "Récupérer le résumé des avis",
        description = "Retourne un résumé des avis (note moyenne, distribution, etc.)"
    )
    public ResponseEntity<Map<String, Object>> getReviewSummary(
            @PathVariable Long attractionId) {
        
        Map<String, Object> statistics = reviewService.getReviewStatistics(attractionId);
        
        // Créer un résumé simplifié
        Map<String, Object> summary = Map.of(
            "averageRating", statistics.get("averageRating"),
            "totalReviews", statistics.get("totalReviews"),
            "verifiedReviews", statistics.get("verifiedReviews"),
            "ratingDistribution", statistics.get("ratingDistribution")
        );
        
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/recent")
    @Operation(
        summary = "Récupérer les avis récents",
        description = "Retourne les avis les plus récents de toutes les attractions"
    )
    public ResponseEntity<List<ReviewDTO>> getRecentReviews(
            @Parameter(description = "Nombre maximum d'avis", example = "10")
            @RequestParam(defaultValue = "10") int limit) {
        
        // Note: Vous devrez créer cette méthode dans le service
        // return ResponseEntity.ok(reviewService.getRecentReviews(limit));
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/top-rated/attractions")
    @Operation(
        summary = "Récupérer les attractions les mieux notées",
        description = "Retourne les attractions avec les meilleures notes moyennes"
    )
    public ResponseEntity<List<Map<String, Object>>> getTopRatedAttractions(
            @Parameter(description = "Nombre maximum d'attractions", example = "5")
            @RequestParam(defaultValue = "5") int limit) {
        
        // Note: Vous devrez créer cette méthode dans le service
        // return ResponseEntity.ok(reviewService.getTopRatedAttractions(limit));
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/search")
    @Operation(
        summary = "Rechercher dans les avis",
        description = "Recherche textuelle dans les titres et commentaires des avis"
    )
    public ResponseEntity<List<ReviewDTO>> searchReviews(
            @Parameter(description = "Terme de recherche", required = true)
            @RequestParam String query) {
        
        // Note: Vous devrez créer cette méthode dans le service
        // return ResponseEntity.ok(reviewService.searchReviews(query));
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @GetMapping("/attraction/{attractionId}/with-images")
    @Operation(
        summary = "Récupérer les avis avec recommandations",
        description = "Retourne les avis les plus utiles avec des recommandations"
    )
    public ResponseEntity<List<ReviewDTO>> getHelpfulReviewsWithRecommendations(
            @PathVariable Long attractionId,
            @RequestParam(defaultValue = "5") int limit) {
        
        // Note: Vous devrez créer cette méthode dans le service
        // return ResponseEntity.ok(reviewService.getHelpfulReviews(attractionId, limit));
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @PatchMapping("/{id}/verify")
    @Operation(
        summary = "Vérifier un avis",
        description = "Marque un avis comme vérifié par l'administrateur"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Avis vérifié"),
        @ApiResponse(responseCode = "404", description = "Avis non trouvé")
    })
    public ResponseEntity<ReviewDTO> verifyReview(
            @PathVariable Long id,
            @RequestParam(defaultValue = "true") boolean verified) {
        
        // Note: Vous devrez créer cette méthode dans le service
        // return ResponseEntity.ok(reviewService.verifyReview(id, verified));
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }
}