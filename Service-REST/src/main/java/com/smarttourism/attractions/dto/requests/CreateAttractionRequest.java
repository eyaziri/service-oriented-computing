package com.smarttourism.attractions.dto.requests;

import com.smarttourism.attractions.Entities.Category;
import com.smarttourism.attractions.Entities.Location;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAttractionRequest {
    
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 3, max = 200, message = "Le nom doit contenir entre 3 et 200 caractères")
    private String name;
    
    @NotBlank(message = "La description est obligatoire")
    @Size(min = 10, max = 2000, message = "La description doit contenir entre 10 et 2000 caractères")
    private String description;
    
    @NotBlank(message = "La ville est obligatoire")
    private String city;
    
    @NotNull(message = "La localisation est obligatoire")
    @Valid
    private Location location;
    
    @NotNull(message = "La catégorie est obligatoire")
    private Category category;
    
    @NotNull(message = "Le prix d'entrée est obligatoire")
    @Min(value = 0, message = "Le prix ne peut pas être négatif")
    private Double entryPrice;
    
    @NotBlank(message = "L'heure d'ouverture est obligatoire")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Format d'heure invalide (HH:mm)")
    private String openingHours;
    
    @NotBlank(message = "L'heure de fermeture est obligatoire")
    @Pattern(regexp = "^([01]\\d|2[0-3]):([0-5]\\d)$", message = "Format d'heure invalide (HH:mm)")
    private String closingHours;
    
    @Min(value = 1, message = "La capacité maximale doit être au moins 1")
    private Integer maxCapacity;
    
    @Min(value = 1, message = "La durée estimée doit être au moins 1 minute")
    private Integer estimatedVisitDuration;
    
    private String imageUrl;
    
    @Pattern(regexp = "^(https?://)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([/\\w \\.-]*)*/?$", 
             message = "URL invalide")
    private String website;
    
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Numéro de téléphone invalide")
    private String phoneNumber;
    
    @Email(message = "Email invalide")
    private String email;
    
    private Boolean isFeatured;
}