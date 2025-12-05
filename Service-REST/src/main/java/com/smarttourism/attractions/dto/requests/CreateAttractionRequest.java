package com.smarttourism.attractions.dto.requests;

import com.smarttourism.attractions.Entities.Category;
import com.smarttourism.attractions.Entities.Location;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAttractionRequest {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 200, message = "Le nom ne doit pas dépasser 200 caractères")
    private String name;
    
    @NotBlank(message = "La ville est obligatoire")
    @Size(max = 100, message = "La ville ne doit pas dépasser 100 caractères")
    private String city;
    
    @Size(max = 2000, message = "La description ne doit pas dépasser 2000 caractères")
    private String description;
    
    @NotNull(message = "La catégorie est obligatoire")
    private Category category;
    
    @NotNull(message = "La localisation est obligatoire")
    private Location location;
    
    @PositiveOrZero(message = "Le prix d'entrée doit être positif ou zéro")
    private Double entryPrice = 0.0;
    
    private LocalTime openingTime;
    private LocalTime closingTime;
    
    @PositiveOrZero(message = "La capacité maximale doit être positive ou zéro")
    private Integer maxCapacity;
    
    @Size(max = 500, message = "L'URL de l'image ne doit pas dépasser 500 caractères")
    private String imageUrl;
    
    @Size(max = 500, message = "L'URL du site web ne doit pas dépasser 500 caractères")
    private String websiteUrl;
    
    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
    private String phoneNumber;
    
    @Size(max = 100, message = "L'email ne doit pas dépasser 100 caractères")
    private String email;
    
    @PositiveOrZero(message = "La durée moyenne de visite doit être positive")
    private Integer averageVisitDuration;
    
    private Boolean isFeatured = false;
}