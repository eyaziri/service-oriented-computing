import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ApiService } from '../../services/api.service';
import { Attraction, Category } from '../../models/attraction.model';
import { Review } from '../../models/review.model';
import { ReservationDialogComponent } from '../reservation-dialog/reservation-dialog.component';
import { ReviewDialogComponent } from '../review-dialog/review-dialog.component';

@Component({
  selector: 'app-attraction-detail',
  templateUrl: './attraction-detail.component.html',
  styleUrls: ['./attraction-detail.component.scss']
})
export class AttractionDetailComponent implements OnInit {
  attractionId!: number;
  attraction!: Attraction;
  reviews: Review[] = [];
  isLoading = true;
  isReviewsLoading = false;
  activeTab = 'overview';
  
  // Variables pour la réservation
  selectedDate: Date | null = null;
  selectedTime: string = 'MORNING';
  selectedPeople: number = 1;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private apiService: ApiService,
    private fb: FormBuilder,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.attractionId = +params['id'];
      this.loadAttraction();
      this.loadReviews();
    });
  }

  loadAttraction(): void {
    this.isLoading = true;
    this.apiService.getAttractionById(this.attractionId).subscribe({
      next: (attraction) => {
        this.attraction = attraction;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading attraction:', error);
        this.isLoading = false;
        this.router.navigate(['/attractions']);
      }
    });
  }

  loadReviews(): void {
  this.isReviewsLoading = true;
  this.apiService.getAttractionReviews(this.attractionId, 0, 10, 'date').subscribe({
    next: (response) => {
      this.reviews = response.content || [];
      this.isReviewsLoading = false;
    },
    error: (error) => {
      console.error('Error loading reviews:', error);
      this.reviews = [];
      this.isReviewsLoading = false;
    }
  });
}

// Ajoutez cette méthode pour calculer la distribution des notes
calculateRatingDistribution(): any[] {
  const distribution = [
    { stars: 5, count: 0, percentage: 0 },
    { stars: 4, count: 0, percentage: 0 },
    { stars: 3, count: 0, percentage: 0 },
    { stars: 2, count: 0, percentage: 0 },
    { stars: 1, count: 0, percentage: 0 }
  ];

  this.reviews.forEach(review => {
    const index = 5 - review.rating;
    if (index >= 0 && index < 5) {
      distribution[index].count++;
    }
  });

  const totalReviews = this.reviews.length;
  if (totalReviews > 0) {
    distribution.forEach(item => {
      item.percentage = Math.round((item.count / totalReviews) * 100);
    });
  }

  return distribution;
}
  openReservationDialog(): void {
    // Préparer les données pour la réservation
    const reservationData = {
      attraction: this.attraction,
      date: this.selectedDate,
      time: this.selectedTime,
      people: this.selectedPeople
    };

    const dialogRef = this.dialog.open(ReservationDialogComponent, {
      width: '600px',
      maxWidth: '90vw',
      data: reservationData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('Réservation créée:', result);
        // Vous pouvez afficher un message de succès ici
      }
    });
  }

  openReviewDialog(): void {
    const dialogRef = this.dialog.open(ReviewDialogComponent, {
      width: '500px',
      maxWidth: '90vw',
      data: { 
        attractionId: this.attractionId,
        attractionName: this.attraction.name
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadReviews();
        this.loadAttraction(); // Mettre à jour la note moyenne
      }
    });
  }

  markReviewAsHelpful(reviewId: number): void {
    this.apiService.markReviewAsHelpful(reviewId).subscribe({
      next: () => {
        const review = this.reviews.find(r => r.id === reviewId);
        if (review) {
          review.helpfulCount++;
        }
      },
      error: (error) => {
        console.error('Error marking review as helpful:', error);
      }
    });
  }

  getStarsArray(rating: number): number[] {
    return Array(5).fill(0).map((_, i) => i < Math.floor(rating) ? 1 : 0);
  }

  getCategoryDisplay(category: string): string {
    return this.apiService.getCategoryDisplay(category as Category);
  }

  getOpeningHours(): string {
    if (!this.attraction.openingHours || !this.attraction.closingHours) {
      return '24h/24';
    }
    return `${this.formatTime(this.attraction.openingHours)} - ${this.formatTime(this.attraction.closingHours)}`;
  }

  private formatTime(timeString: string): string {
    if (!timeString) return '';
    const [hours, minutes] = timeString.split(':');
    return `${hours}:${minutes}`;
  }

  getOccupancyClass(): string {
    const rate = this.attraction.occupancyRate || 0;
    if (rate < 30) return 'low';
    if (rate < 70) return 'medium';
    return 'high';
  }

  shareAttraction(): void {
    if (navigator.share) {
      navigator.share({
        title: this.attraction.name,
        text: this.attraction.description,
        url: window.location.href
      }).catch(error => {
        console.log('Error sharing:', error);
        this.copyToClipboard();
      });
    } else {
      this.copyToClipboard();
    }
  }

  private copyToClipboard(): void {
    navigator.clipboard.writeText(window.location.href).then(() => {
      // Afficher un message de succès (vous pouvez utiliser MatSnackBar)
      console.log('Lien copié dans le presse-papier');
    });
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;
  }

  // Calculer la distribution des notes pour le graphique
  

  // Getters pour la date
  get minDate(): Date {
    return new Date();
  }

  get maxDate(): Date {
    const date = new Date();
    date.setFullYear(date.getFullYear() + 1);
    return date;
  }
}