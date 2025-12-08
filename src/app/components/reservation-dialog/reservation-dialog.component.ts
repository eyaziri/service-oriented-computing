import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../services/api.service';
import { ReservationStatus } from '../../models/reservation.model';

interface ReservationDialogData {
  attraction: any;
  date?: Date;
  time?: string;
  people?: number;
}

@Component({
  selector: 'app-reservation-dialog',
  templateUrl: './reservation-dialog.component.html',
  styleUrls: ['./reservation-dialog.component.scss']
})
export class ReservationDialogComponent implements OnInit {
  reservationForm: FormGroup;
  isLoading = false;
  isSubmitting = false;
  
  // Options pour le nombre de personnes
  peopleOptions = Array.from({length: 10}, (_, i) => i + 1);
  
  // Options pour les heures de visite
  timeSlots = [
    { value: 'MORNING', label: 'Matin (9h-12h)', icon: 'wb_sunny' },
    { value: 'AFTERNOON', label: 'Après-midi (14h-17h)', icon: 'brightness_5' },
    { value: 'FULL_DAY', label: 'Journée entière (9h-17h)', icon: 'schedule' }
  ];

  // Pour simuler un utilisateur connecté
  currentUser = {
    id: 'tourist-123',
    name: 'Jean Dupont',
    email: 'jean.dupont@example.com',
    phone: '0612345678',
    country: 'France'
  };

  constructor(
    public dialogRef: MatDialogRef<ReservationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ReservationDialogData,
    private fb: FormBuilder,
    public apiService: ApiService,
    private snackBar: MatSnackBar
  ) {
    this.reservationForm = this.fb.group({
      attractionId: [data.attraction?.id || '', Validators.required],
      touristId: [this.currentUser.id, Validators.required],
      touristName: [this.currentUser.name, [Validators.required, Validators.minLength(2)]],
      touristEmail: [this.currentUser.email, [Validators.required, Validators.email]],
      touristPhone: [this.currentUser.phone, Validators.pattern(/^[0-9]{10}$/)],
      touristCountry: [this.currentUser.country],
      visitDate: [data.date || '', Validators.required],
      visitTime: [data.time || 'MORNING', Validators.required],
      numberOfPeople: [data.people || 1, [Validators.required, Validators.min(1), Validators.max(20)]],
      specialRequirements: [''],
      status: [ReservationStatus.CONFIRMED]
    });
  }

  ngOnInit(): void {
    // Calculer le prix total initial
    this.calculateTotalPrice();
    
    // Écouter les changements pour recalculer le prix
    this.reservationForm.get('numberOfPeople')?.valueChanges.subscribe(() => {
      this.calculateTotalPrice();
    });
  }

  calculateTotalPrice(): void {
    const people = this.reservationForm.get('numberOfPeople')?.value || 1;
    const pricePerPerson = this.data.attraction?.entryPrice || 0;
    const totalPrice = people * pricePerPerson;
    
    // Mettre à jour le formulaire (mais pas le champ directement car il n'existe pas)
    // Nous utiliserons une propriété calculée
  }

  get totalPrice(): number {
    const people = this.reservationForm.get('numberOfPeople')?.value || 1;
    const pricePerPerson = this.data.attraction?.entryPrice || 0;
    return people * pricePerPerson;
  }

  get pricePerPerson(): number {
    return this.data.attraction?.entryPrice || 0;
  }

  get minDate(): Date {
    const today = new Date();
    // Démain au minimum
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    return tomorrow;
  }

  get maxDate(): Date {
    const max = new Date();
    max.setMonth(max.getMonth() + 6); // 6 mois max à l'avance
    return max;
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.reservationForm.valid) {
      this.isSubmitting = true;
      
      // Préparer les données pour l'API
      const formData = {
        ...this.reservationForm.value,
        totalPrice: this.totalPrice,
        reservationTime: new Date().toISOString()
      };

      // Simuler un délai d'envoi
      setTimeout(() => {
        this.apiService.createReservation(formData).subscribe({
          next: (reservation) => {
            this.isSubmitting = false;
            this.snackBar.open('Réservation créée avec succès!', 'Fermer', {
              duration: 3000,
              panelClass: ['success-snackbar']
            });
            this.dialogRef.close(reservation);
          },
          error: (error) => {
            console.error('Error creating reservation:', error);
            this.isSubmitting = false;
            this.snackBar.open('Erreur lors de la réservation', 'Fermer', {
              duration: 3000,
              panelClass: ['error-snackbar']
            });
          }
        });
      }, 1500); // Simulation de délai
    } else {
      // Marquer tous les champs comme touchés pour afficher les erreurs
      Object.keys(this.reservationForm.controls).forEach(key => {
        const control = this.reservationForm.get(key);
        control?.markAsTouched();
      });
    }
  }

  getTimeSlotIcon(timeSlot: string): string {
    const slot = this.timeSlots.find(s => s.value === timeSlot);
    return slot?.icon || 'schedule';
  }

  getTimeSlotLabel(timeSlot: string): string {
    const slot = this.timeSlots.find(s => s.value === timeSlot);
    return slot?.label || timeSlot;
  }
}