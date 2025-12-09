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
  
  timeOptions: string[] = this.generateTimeOptions();
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
    this.syncCityFields();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(200)]],
      description: ['', Validators.maxLength(2000)],
      category: ['', Validators.required],
      imageUrl: ['', Validators.pattern('https?://.+')],
      
      city: ['', [Validators.required, Validators.maxLength(100)]],
      location: this.fb.group({
        latitude: [null],
        longitude: [null],
        address: [''],
        postalCode: [''],
        city: ['', [Validators.maxLength(100)]],
        country: ['', [Validators.required, Validators.maxLength(100)]]
      }),
      
      entryPrice: [null, [Validators.min(0)]],
      // ✅ CORRECTION: Utiliser openingHours et closingHours (pas Time)
      openingHours: ['09:00'],
      closingHours: ['18:00'],
      maxCapacity: [null, [Validators.min(1), Validators.max(10000)]],
      averageVisitDuration: [null, [Validators.min(5), Validators.max(480)]],
      
      websiteUrl: ['', Validators.pattern('https?://.+')],
      phoneNumber: ['', Validators.pattern(/^[\d\s\-\+\(\)]+$/)],
      email: ['', Validators.email],
      isActive: [true],
      isFeatured: [false],
    });
  }

  private syncCityFields(): void {
    this.attractionForm.get('city')?.valueChanges.subscribe(value => {
      this.attractionForm.get('location.city')?.setValue(value, { emitEvent: false });
    });

    this.attractionForm.get('location.city')?.valueChanges.subscribe(value => {
      this.attractionForm.get('city')?.setValue(value, { emitEvent: false });
    });
  }

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

  onCityChange(event: any): void {
    const cityValue = event.target?.value || '';
    this.attractionForm.get('location.city')?.setValue(cityValue, { emitEvent: false });
  }

  private patchFormWithAttraction(attraction: Attraction): void {
    if (!attraction) return;

    this.attractionForm.patchValue({
      name: attraction.name || '',
      description: attraction.description || '',
      category: attraction.category || '',
      imageUrl: attraction.imageUrl || ''
    });

    this.attractionForm.patchValue({
      city: attraction.city || ''
    });

    if (attraction.location) {
      this.attractionForm.get('location')?.patchValue({
        latitude: attraction.location.latitude || null,
        longitude: attraction.location.longitude || null,
        address: attraction.location.address || '',
        postalCode: attraction.location.postalCode || '',
        city: attraction.location.city || attraction.city || '',
        country: attraction.location.country || ''
      });
    } else {
      this.attractionForm.get('location.city')?.setValue(attraction.city || '');
    }

    // ✅ CORRECTION: Utiliser openingHours et closingHours
    this.attractionForm.patchValue({
      entryPrice: attraction.entryPrice,
      openingHours: attraction.openingHours || '09:00',
      closingHours: attraction.closingHours || '18:00',
      maxCapacity: attraction.maxCapacity,
      averageVisitDuration: attraction.averageVisitDuration
    });

    this.attractionForm.patchValue({
      websiteUrl: attraction.websiteUrl || '',
      phoneNumber: attraction.phoneNumber || '',
      email: attraction.email || '',
      isActive: attraction.isActive !== undefined ? attraction.isActive : true,
      isFeatured: attraction.isFeatured || false
    });

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
  const formValue = this.attractionForm.value;
  
  // ✅ FORCEZ LE FORMAT EXACT COMME POSTMAN
  const apiData = {
    name: formValue.name.trim(),
    description: formValue.description?.trim() || '',
    category: formValue.category,
    city: formValue.city.trim(),
    location: {
      latitude: formValue.location?.latitude ? 
        parseFloat(formValue.location.latitude).toFixed(4) : "36.8093",  // ← .toFixed(4)
      longitude: formValue.location?.longitude ? 
        parseFloat(formValue.location.longitude).toFixed(4) : "10.1341", // ← .toFixed(4)
      address: formValue.location?.address?.trim() || '',
      postalCode: formValue.location?.postalCode?.trim() || '',
      city: formValue.city.trim(),
      country: formValue.location?.country?.trim() || 'Tunisie'
    },
    // ✅ IMPORTANT: Forcez en nombre décimal comme Postman (10.0, pas 10)
    entryPrice: formValue.entryPrice ? 
      parseFloat(formValue.entryPrice).toFixed(1) : "10.0",  // ← .toFixed(1)
    
    openingHours: formValue.openingHours || '09:00',
    closingHours: formValue.closingHours || '17:00',  // ← 17:00 comme Postman
    
    maxCapacity: formValue.maxCapacity || 500,
    imageUrl: formValue.imageUrl?.trim() || '',
    websiteUrl: formValue.websiteUrl?.trim() || '',
    
    // ✅ Assurez-vous du format exact du téléphone
    phoneNumber: this.formatPhoneNumber(formValue.phoneNumber?.trim() || ''),
    
    email: formValue.email?.trim() || '',
    
    // ✅ Forcez en nombre décimal aussi
    averageVisitDuration: formValue.averageVisitDuration || 120,
    
    isActive: formValue.isActive !== undefined ? formValue.isActive : true,
    isFeatured: formValue.isFeatured || false
  };
  
  console.log('📤 JSON CORRIGÉ comme Postman:');
  console.log(JSON.stringify(apiData, null, 2));

  if (this.data.mode === 'create') {
    this.createAttraction(apiData);
  } else {
    this.updateAttraction(apiData);
  }
}

// Ajoutez cette méthode pour formater le téléphone
private formatPhoneNumber(phone: string): string {
  if (!phone) return '+21612345678'; // Valeur par défaut comme Postman
  
  // Supprimez tous les espaces
  phone = phone.replace(/\s/g, '');
  
  // Assurez-vous qu'il commence par +
  if (!phone.startsWith('+')) {
    phone = '+' + phone;
  }
  
  return phone;
}

  private logFormErrors(formGroup: FormGroup, parentKey = ''): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      const fullKey = parentKey ? `${parentKey}.${key}` : key;
      
      if (control instanceof FormGroup) {
        this.logFormErrors(control, fullKey);
      } else {
        const errors = control?.errors;
        if (errors) {
          console.log(`  ${fullKey}:`, errors);
        }
      }
    });
  }

  private createAttraction(data: any): void {
    this.apiService.createAttraction(data).subscribe({
      next: (response) => {
        this.loading = false;
        this.dialogRef.close(response);
        this.snackBar.open('✅ Attraction créée avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (error) => {
        this.loading = false;
        console.error('❌ ERREUR COMPLÈTE:', error);
        console.error('❌ Status:', error.status);
        console.error('❌ Error body:', error.error);
        console.error('❌ Error message:', error.message);
        
        let errorMessage = 'Erreur lors de la création';
        
        if (error.status === 400) {
          if (error.error?.message) {
            errorMessage = error.error.message;
          } else if (error.error?.errors) {
            const errors = Object.entries(error.error.errors)
              .map(([field, msg]) => `${field}: ${msg}`)
              .join(', ');
            errorMessage = `Erreurs de validation: ${errors}`;
          } else if (typeof error.error === 'string') {
            errorMessage = error.error;
          } else {
            errorMessage = 'Validation échouée. Vérifiez tous les champs.';
          }
        } else if (error.error?.message) {
          errorMessage = error.error.message;
        }
        
        this.snackBar.open(`❌ ${errorMessage}`, 'Fermer', { 
          duration: 8000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  private updateAttraction(data: any): void {
    if (!this.data.attraction?.id) return;
    
    this.apiService.updateAttraction(this.data.attraction.id, data).subscribe({
      next: (response) => {
        this.loading = false;
        this.dialogRef.close(response);
        this.snackBar.open('✅ Attraction mise à jour avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (error) => {
        this.loading = false;
        console.error('❌ Error updating attraction:', error);
        
        let errorMessage = 'Erreur lors de la mise à jour';
        if (error.error?.message) {
          errorMessage = error.error.message;
        } else if (error.message) {
          errorMessage = error.message;
        }
        
        this.snackBar.open(errorMessage, 'Fermer', { 
          duration: 5000,
          panelClass: ['error-snackbar']
        });
      }
    });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getCategoryDisplay(category: Category): string {
    return this.apiService.getCategoryDisplay(category);
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.values(formGroup.controls).forEach(control => {
      control.markAsTouched();
      
      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  debugForm(): void {
    console.log('🐛 DEBUG DU FORMULAIRE');
    console.log('----------------------');
    
    const formValue = this.attractionForm.value;
    
    console.log('🎯 ÉTAT DU FORMULAIRE:');
    console.log('- Valide:', this.attractionForm.valid);
    console.log('- Touched:', this.attractionForm.touched);
    console.log('- Dirty:', this.attractionForm.dirty);
    console.log('- Pristine:', this.attractionForm.pristine);
    
    console.log('\n📋 VALEURS:');
    console.table(formValue);
    
    console.log('\n❌ ERREURS:');
    this.logFormErrors(this.attractionForm);
    
    console.log('\n🔍 CONTRÔLES INDIVIDUELS:');
    Object.keys(formValue).forEach(key => {
      const control = this.attractionForm.get(key);
      if (control) {
        console.log(`${key}:`, {
          value: control.value,
          valid: control.valid,
          invalid: control.invalid,
          errors: control.errors,
          touched: control.touched,
          dirty: control.dirty
        });
      }
    });
  }
}