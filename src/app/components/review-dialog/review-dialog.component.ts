import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../services/api.service';

interface ReviewDialogData {
  attractionId: number;
  attractionName: string;
  reservationId?: number;
}

@Component({
  selector: 'app-review-dialog',
  templateUrl: './review-dialog.component.html',
  styleUrls: ['./review-dialog.component.scss']
})
export class ReviewDialogComponent implements OnInit {
  reviewForm: FormGroup;
  isSubmitting = false;
  selectedRating = 5;
  
  // Pour simuler un utilisateur connecté
  currentUser = {
    id: 'tourist-123',
    name: 'Jean Dupont',
    country: 'France'
  };

  constructor(
    public dialogRef: MatDialogRef<ReviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ReviewDialogData,
    private fb: FormBuilder,
    private apiService: ApiService,
    private snackBar: MatSnackBar
  ) {
    this.reviewForm = this.fb.group({
      attractionId: [data.attractionId, Validators.required],
      reservationId: [data.reservationId || null],
      touristId: [this.currentUser.id, Validators.required],
      touristName: [this.currentUser.name, Validators.required],
      touristCountry: [this.currentUser.country],
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      title: ['', [Validators.maxLength(200)]],
      comment: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(2000)]],
      visitDate: [''],
      isVerifiedVisit: [!!data.reservationId]
    });
  }

  ngOnInit(): void {
    // Initialiser avec la note par défaut
    this.setRating(5);
  }

  setRating(rating: number): void {
    this.selectedRating = rating;
    this.reviewForm.patchValue({ rating });
  }

  getStarsArray(): number[] {
    return Array(5).fill(0).map((_, i) => i);
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.reviewForm.valid) {
      this.isSubmitting = true;
      
      // Préparer les données pour l'API
      const formData = {
        ...this.reviewForm.value,
        reviewDate: new Date().toISOString()
      };

      // Simuler un délai d'envoi
      setTimeout(() => {
        this.apiService.createReview(formData).subscribe({
          next: (review) => {
            this.isSubmitting = false;
            this.snackBar.open('Avis publié avec succès!', 'Fermer', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
            this.dialogRef.close(review);
          },
          error: (error) => {
            console.error('Error creating review:', error);
            this.isSubmitting = false;
            this.snackBar.open('Erreur lors de la publication', 'Fermer', {
              duration: 3000,
              panelClass: ['error-snackbar']
            });
          }
        });
      }, 1500);
    } else {
      // Marquer tous les champs comme touchés
      Object.keys(this.reviewForm.controls).forEach(key => {
        const control = this.reviewForm.get(key);
        control?.markAsTouched();
      });
    }
  }

  get minDate(): Date {
    const min = new Date();
    min.setFullYear(min.getFullYear() - 1); // Jusqu'à 1 an dans le passé
    return min;
  }

  get maxDate(): Date {
    return new Date();
  }

  get characterCount(): number {
    return this.reviewForm.get('comment')?.value?.length || 0;
  }

  get characterLimit(): number {
    return 2000;
  }

  get characterCountClass(): string {
    const count = this.characterCount;
    if (count < 10) return 'error';
    if (count > 1900) return 'warning';
    return '';
  }
}