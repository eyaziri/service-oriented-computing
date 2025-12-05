package com.smarttourism.attractions.controller;

import com.smarttourism.attractions.dto.AttractionDTO;
import com.smarttourism.attractions.dto.requests.CreateAttractionRequest;
import com.smarttourism.attractions.Entities.Category;
import com.smarttourism.attractions.service.AttractionService;
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
@RequestMapping("/api/attractions")
@RequiredArgsConstructor
@Tag(name = "Attractions", description = "Endpoints pour la gestion des attractions touristiques")
public class AttractionController {
    
    private final AttractionService attractionService;
    
    @PostMapping
    @Operation(summary = "Créer une nouvelle attraction", description = "Ajoute une nouvelle attraction touristique à la plateforme")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Attraction créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "409", description = "Attraction déjà existante")
    })
    public ResponseEntity<AttractionDTO> createAttraction(
            @Valid @RequestBody CreateAttractionRequest request) {
        AttractionDTO created = attractionService.createAttraction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping
    @Operation(summary = "Récupérer toutes les attractions", description = "Retourne la liste de toutes les attractions actives")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des attractions récupérée"),
        @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    public ResponseEntity<List<AttractionDTO>> getAllAttractions() {
        return ResponseEntity.ok(attractionService.getAllAttractions());
    }
    
    @GetMapping("/paginated")
    @Operation(summary = "Récupérer les attractions avec pagination", description = "Retourne les attractions avec pagination et tri")
    public ResponseEntity<Page<AttractionDTO>> getAllAttractionsPaginated(
            @Parameter(description = "Numéro de page (0-based)", example = "0") 
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Taille de la page", example = "10") 
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Champ de tri", example = "name") 
            @RequestParam(defaultValue = "name") String sortBy,
            @Parameter(description = "Ordre de tri", example = "asc") 
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("desc") ? 
            Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<AttractionDTO> attractions = attractionService.getAllAttractions(pageable);
        return ResponseEntity.ok(attractions);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer une attraction par ID", description = "Retourne les détails d'une attraction spécifique")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attraction trouvée"),
        @ApiResponse(responseCode = "404", description = "Attraction non trouvée")
    })
    public ResponseEntity<AttractionDTO> getAttractionById(
            @Parameter(description = "ID de l'attraction", required = true, example = "1")
            @PathVariable Long id) {
        return ResponseEntity.ok(attractionService.getAttractionById(id));
    }
    
    @GetMapping("/city/{city}")
    @Operation(summary = "Récupérer les attractions par ville", description = "Retourne toutes les attractions d'une ville spécifique")
    public ResponseEntity<List<AttractionDTO>> getAttractionsByCity(
            @Parameter(description = "Nom de la ville", required = true, example = "Tunis")
            @PathVariable String city) {
        return ResponseEntity.ok(attractionService.getAttractionsByCity(city));
    }
    
    @GetMapping("/city/{city}/paginated")
    @Operation(summary = "Récupérer les attractions par ville avec pagination")
    public ResponseEntity<Page<AttractionDTO>> getAttractionsByCityPaginated(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(attractionService.getAttractionsByCity(city, pageable));
    }
    
    @GetMapping("/category/{category}")
    @Operation(summary = "Récupérer les attractions par catégorie", description = "Retourne toutes les attractions d'une catégorie spécifique")
    public ResponseEntity<List<AttractionDTO>> getAttractionsByCategory(
            @Parameter(description = "Catégorie de l'attraction", required = true, example = "MUSEUM")
            @PathVariable Category category) {
        return ResponseEntity.ok(attractionService.getAttractionsByCategory(category));
    }
    
    @GetMapping("/search")
    @Operation(summary = "Rechercher des attractions avec filtres", description = "Recherche d'attractions avec plusieurs filtres optionnels")
    public ResponseEntity<Page<AttractionDTO>> searchAttractions(
            @Parameter(description = "Ville de recherche") 
            @RequestParam(required = false) String city,
            @Parameter(description = "Catégorie de recherche") 
            @RequestParam(required = false) Category category,
            @Parameter(description = "Prix minimum") 
            @RequestParam(required = false) Double minPrice,
            @Parameter(description = "Prix maximum") 
            @RequestParam(required = false) Double maxPrice,
            @Parameter(description = "Note minimum") 
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<AttractionDTO> results = attractionService.searchAttractions(
            city, category, minPrice, maxPrice, minRating, pageable);
        return ResponseEntity.ok(results);
    }
    
    @GetMapping("/search/quick")
    @Operation(summary = "Recherche rapide d'attractions", description = "Recherche textuelle dans les noms, descriptions et villes")
    public ResponseEntity<List<AttractionDTO>> quickSearch(
            @Parameter(description = "Terme de recherche", required = true)
            @RequestParam String query) {
        return ResponseEntity.ok(attractionService.searchAttractions(query));
    }
    
    @GetMapping("/top-rated")
    @Operation(summary = "Récupérer les attractions les mieux notées", description = "Retourne les attractions avec les meilleures notes")
    public ResponseEntity<List<AttractionDTO>> getTopRatedAttractions() {
        return ResponseEntity.ok(attractionService.getTopRatedAttractions());
    }
    
    @GetMapping("/featured")
    @Operation(summary = "Récupérer les attractions en vedette", description = "Retourne les attractions marquées comme vedettes")
    public ResponseEntity<List<AttractionDTO>> getFeaturedAttractions() {
        return ResponseEntity.ok(attractionService.getFeaturedAttractions());
    }
    
    @GetMapping("/cities")
    @Operation(summary = "Récupérer toutes les villes", description = "Retourne la liste de toutes les villes disponibles")
    public ResponseEntity<List<String>> getAllCities() {
        return ResponseEntity.ok(attractionService.getAllCities());
    }
    
    @GetMapping("/categories")
    @Operation(summary = "Récupérer toutes les catégories", description = "Retourne la liste de toutes les catégories disponibles")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(attractionService.getAllCategories());
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "Récupérer les statistiques", description = "Retourne les statistiques générales des attractions")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        return ResponseEntity.ok(attractionService.getStatistics());
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une attraction", description = "Met à jour les informations d'une attraction existante")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Attraction mise à jour"),
        @ApiResponse(responseCode = "404", description = "Attraction non trouvée"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<AttractionDTO> updateAttraction(
            @PathVariable Long id,
            @Valid @RequestBody CreateAttractionRequest request) {
        return ResponseEntity.ok(attractionService.updateAttraction(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une attraction", description = "Désactive une attraction (soft delete)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Attraction supprimée"),
        @ApiResponse(responseCode = "404", description = "Attraction non trouvée")
    })
    public ResponseEntity<Void> deleteAttraction(@PathVariable Long id) {
        attractionService.deleteAttraction(id);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{id}/visitors")
    @Operation(summary = "Mettre à jour le nombre de visiteurs", description = "Met à jour le nombre actuel de visiteurs pour une attraction")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Visiteurs mis à jour"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "404", description = "Attraction non trouvée")
    })
    public ResponseEntity<Void> updateVisitors(
            @PathVariable Long id,
            @RequestParam Integer visitorCount) {
        attractionService.updateCurrentVisitors(id, visitorCount);
        return ResponseEntity.ok().build();
    }
}