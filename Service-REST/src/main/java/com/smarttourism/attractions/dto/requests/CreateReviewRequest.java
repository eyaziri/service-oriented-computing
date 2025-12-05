package com.smarttourism.attractions.dto.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReviewRequest {
    
    @NotNull(message = "L'ID de l'attraction est obligatoire")
    private Long attractionId;
    
    private Long reservationId;
    
    @NotBlank(message = "L'ID du touriste est obligatoire")
    private String touristId;
    
    @NotBlank(message = "Le nom du touriste est obligatoire")
    @Size(max = 200, message = "Le nom ne doit pas dépasser 200 caractères")
    private String touristName;
    
    @Size(max = 100, message = "Le pays ne doit pas dépasser 100 caractères")
    private String touristCountry;
    
    @NotNull(message = "La note est obligatoire")
    @Min(value = 1, message = "La note doit être au moins 1")
    @Max(value = 5, message = "La note ne peut pas dépasser 5")
    private Integer rating;
    
    @Size(max = 200, message = "Le titre ne doit pas dépasser 200 caractères")
    private String title;
    
    @Size(max = 2000, message = "Le commentaire ne doit pas dépasser 2000 caractères")
    private String comment;
    
    private LocalDate visitDate;
}