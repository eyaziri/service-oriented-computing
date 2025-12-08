import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatStepper } from '@angular/material/stepper';
import { ApiService } from '../services/api.service';
import { Attraction, Category } from '../models/attraction.model';

export interface AttractionFormDialogData {
  mode: 'create' | 'edit';
  attraction?: Attraction;
}

@Component({
  selector: 'app-attraction-form-dialog',
  templateUrl: './attraction-form-dialog.component.html',
  styleUrls: ['./attraction-form-dialog.component.scss']
})
export class AttractionFormDialogComponent implements OnInit {
  attractionForm: FormGroup;
  loading = false;
  categories = Object.values(Category);
  
  // Time options
  timeOptions: string[] = this.generateTimeOptions();
  
  // Image preview
  imagePreview: string | null = null;

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private snackBar: MatSnackBar,
    public dialogRef: MatDialogRef<AttractionFormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: AttractionFormDialogData
  ) {
    this.attractionForm = this.createForm();
  }

  ngOnInit() {
    if (this.data.mode === 'edit' && this.data.attraction) {
      this.patchFormWithAttraction(this.data.attraction);
    }
    
    // Synchroniser le champ city à la racine avec location.city
    this.syncCityFields();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      // Étape 1 - Informations de base
      name: ['', [Validators.required, Validators.maxLength(200)]],
      description: ['', Validators.maxLength(2000)],
      category: ['', Validators.required],
      imageUrl: ['', Validators.pattern('https?://.+')],
      
      // Étape 2 - Localisation
      city: ['', [Validators.required, Validators.maxLength(100)]], // Champ à la racine (pour le JSON)
      location: this.fb.group({
        latitude: [null],
        longitude: [null],
        address: [''],
        postalCode: [''],
        city: ['', [Validators.maxLength(100)]], // Pas required ici, sera synchronisé
        country: ['', [Validators.required, Validators.maxLength(100)]]
      }),
      
      // Étape 3 - Détails
      entryPrice: [null, [Validators.min(0)]],
      openingHours: ['09:00'],
      closingHours: ['18:00'],
      maxCapacity: [null, [Validators.min(1), Validators.max(10000)]],
      averageVisitDuration: [null, [Validators.min(5), Validators.max(480)]],
      
      // Étape 4 - Contact
      websiteUrl: ['', Validators.pattern('https?://.+')],
      phoneNumber: ['', Validators.pattern(/^[\d\s\-\+\(\)]+$/)],
      email: ['', Validators.email],
      isActive: [true],
      isFeatured: [false],
    });
  }

  // Synchroniser les champs city
  private syncCityFields(): void {
    // Lorsque city à la racine change, mettre à jour location.city
    this.attractionForm.get('city')?.valueChanges.subscribe(value => {
      this.attractionForm.get('location.city')?.setValue(value, { emitEvent: false });
    });

    // Lorsque location.city change, mettre à jour city à la racine
    this.attractionForm.get('location.city')?.valueChanges.subscribe(value => {
      this.attractionForm.get('city')?.setValue(value, { emitEvent: false });
    });
  }

  // Navigation method
  goToNextStep(stepper: MatStepper, stepNumber: number): void {
    switch(stepNumber) {
      case 1:
        if (this.attractionForm.get('name')?.valid && 
            this.attractionForm.get('category')?.valid) {
          stepper.next();
        } else {
          this.attractionForm.get('name')?.markAsTouched();
          this.attractionForm.get('category')?.markAsTouched();
        }
        break;
      case 2:
        const cityValid = this.attractionForm.get('city')?.valid;
        const countryValid = this.attractionForm.get('location.country')?.valid;
        
        if (cityValid && countryValid) {
          stepper.next();
        } else {
          this.attractionForm.get('city')?.markAsTouched();
          this.attractionForm.get('location.country')?.markAsTouched();
        }
        break;
      default:
        stepper.next();
    }
  }

  // Gérer le changement de ville
  onCityChange(event: any): void {
    const cityValue = event.target?.value || '';
    // Synchroniser les champs
    this.attractionForm.get('location.city')?.setValue(cityValue, { emitEvent: false });
  }

  private patchFormWithAttraction(attraction: Attraction): void {
    if (!attraction) return;

    // Étape 1 - Informations de base
    this.attractionForm.patchValue({
      name: attraction.name || '',
      description: attraction.description || '',
      category: attraction.category || '',
      imageUrl: attraction.imageUrl || ''
    });

    // Étape 2 - Localisation
    // Remplir le champ city à la racine (vient de attraction.city)
    this.attractionForm.patchValue({
      city: attraction.city || ''
    });

    // Remplir l'objet location
    if (attraction.location) {
      this.attractionForm.get('location')?.patchValue({
        latitude: attraction.location.latitude || null,
        longitude: attraction.location.longitude || null,
        address: attraction.location.address || '',
        postalCode: attraction.location.postalCode || '',
        city: attraction.location.city || attraction.city || '', // Synchronisation automatique
        country: attraction.location.country || ''
      });
    } else {
      // Si pas de location, utiliser city à la racine
      this.attractionForm.get('location.city')?.setValue(attraction.city || '');
    }

    // Étape 3 - Détails
    this.attractionForm.patchValue({
      entryPrice: attraction.entryPrice,
      openingHours: attraction.openingTime || '09:00',
      closingHours: attraction.closingTime || '18:00',
      maxCapacity: attraction.maxCapacity,
      averageVisitDuration: attraction.averageVisitDuration
    });

    // Étape 4 - Contact
    this.attractionForm.patchValue({
      websiteUrl: attraction.websiteUrl || '',
      phoneNumber: attraction.phoneNumber || '',
      email: attraction.email || '',
      isActive: attraction.isActive !== undefined ? attraction.isActive : true,
      isFeatured: attraction.isFeatured || false
    });

    // Image preview
    if (attraction.imageUrl) {
      this.imagePreview = attraction.imageUrl;
    }
  }

  private generateTimeOptions(): string[] {
    const times: string[] = [];
    for (let hour = 0; hour < 24; hour++) {
      for (let minute = 0; minute < 60; minute += 30) {
        const hourStr = hour.toString().padStart(2, '0');
        const minuteStr = minute.toString().padStart(2, '0');
        times.push(`${hourStr}:${minuteStr}`);
      }
    }
    return times;
  }

  onImageUrlChange(): void {
    const imageUrl = this.attractionForm.get('imageUrl')?.value;
    if (imageUrl) {
      this.imagePreview = imageUrl;
    } else {
      this.imagePreview = null;
    }
  }

  onSubmit(): void {
    if (this.attractionForm.invalid) {
      this.markFormGroupTouched(this.attractionForm);
      return;
    }

    this.loading = true;
    
    const formValue = this.attractionForm.value;
    
    // Formatage correct pour l'API - selon le JSON fourni
    const apiData = {
      name: formValue.name,
      description: formValue.description || '',
      category: formValue.category,
      
      // Champ city à la racine (important pour le backend)
      city: formValue.city,
      
      // Location complète
      location: {
        latitude: formValue.location.latitude ? parseFloat(formValue.location.latitude) : null,
        longitude: formValue.location.longitude ? parseFloat(formValue.location.longitude) : null,
        address: formValue.location.address || '',
        postalCode: formValue.location.postalCode || '',
        city: formValue.location.city || formValue.city, // S'assurer que city est dans location aussi
        country: formValue.location.country || ''
      },
      
      // Détails avec conversion
      entryPrice: formValue.entryPrice ? parseFloat(formValue.entryPrice) : null,
      openingTime: formValue.openingHours || '09:00',
      closingTime: formValue.closingHours || '18:00',
      maxCapacity: formValue.maxCapacity ? parseInt(formValue.maxCapacity) : null,
      
      // Contact et URLs
      imageUrl: formValue.imageUrl || '',
      websiteUrl: formValue.websiteUrl || '',
      phoneNumber: formValue.phoneNumber || '',
      email: formValue.email || '',
      
      // Informations supplémentaires
      averageVisitDuration: formValue.averageVisitDuration ? parseInt(formValue.averageVisitDuration) : null,
      isActive: formValue.isActive !== undefined ? formValue.isActive : true,
      isFeatured: formValue.isFeatured || false
    };

    console.log('Données envoyées à l\'API:', JSON.stringify(apiData, null, 2));

    if (this.data.mode === 'create') {
      this.createAttraction(apiData);
    } else {
      this.updateAttraction(apiData);
    }
  }

  private createAttraction(data: any): void {
    this.apiService.createAttraction(data).subscribe({
      next: (response) => {
        this.loading = false;
        this.dialogRef.close(response);
        this.snackBar.open('Attraction créée avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (error) => {
        this.loading = false;
        console.error('Error creating attraction:', error);
        console.error('Error response:', error.error);
        this.snackBar.open(
          error.error?.message || 'Erreur lors de la création', 
          'Fermer', 
          { duration: 5000 }
        );
      }
    });
  }

  private updateAttraction(data: any): void {
    if (!this.data.attraction?.id) return;
    
    this.apiService.updateAttraction(this.data.attraction.id, data).subscribe({
      next: (response) => {
        this.loading = false;
        this.dialogRef.close(response);
        this.snackBar.open('Attraction mise à jour avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (error) => {
        this.loading = false;
        console.error('Error updating attraction:', error);
        console.error('Error response:', error.error);
        this.snackBar.open(
          error.error?.message || 'Erreur lors de la mise à jour', 
          'Fermer', 
          { duration: 5000 }
        );
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getCategoryDisplay(category: Category): string {
    return this.apiService.getCategoryDisplay(category);
  }

  // Helper to mark all fields as touched for validation
  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }
}