package com.smarttourism.attractions.dto.requests;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateReservationRequest {
    
    @NotNull(message = "L'ID de l'attraction est obligatoire")
    private Long attractionId;
    
    @NotBlank(message = "L'ID du touriste est obligatoire")
    private String touristId;
    
    @NotBlank(message = "Le nom du touriste est obligatoire")
    @Size(max = 200, message = "Le nom ne doit pas dépasser 200 caractères")
    private String touristName;
    
    @NotBlank(message = "L'email du touriste est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 200, message = "L'email ne doit pas dépasser 200 caractères")
    private String touristEmail;
    
    @Size(max = 20, message = "Le téléphone ne doit pas dépasser 20 caractères")
    private String touristPhone;
    
    @Size(max = 100, message = "Le pays ne doit pas dépasser 100 caractères")
    private String touristCountry;
    
    @NotNull(message = "La date de visite est obligatoire")
    @FutureOrPresent(message = "La date de visite doit être aujourd'hui ou dans le futur")
    private LocalDate visitDate;
    
    private String visitTime;
    
    @NotNull(message = "Le nombre de personnes est obligatoire")
    @Min(value = 1, message = "Le nombre de personnes doit être au moins 1")
    @Max(value = 50, message = "Le nombre de personnes ne peut pas dépasser 50")
    private Integer numberOfPeople = 1;
    
    @Size(max = 500, message = "Les exigences spéciales ne doivent pas dépasser 500 caractères")
    private String specialRequirements;
}