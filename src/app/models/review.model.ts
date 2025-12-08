export interface Review {
  id: number;
  attractionId: number;
  reservationId?: number;
  touristId: string;
  touristName: string;
  touristCountry?: string;
  rating: number;
  title?: string;
  comment: string;
  reviewDate: string;
  visitDate?: string;
  isVerifiedVisit: boolean;
  helpfulCount: number;
  reply?: string;
  repliedAt?: string;
  isEdited: boolean;
  editedAt?: string;
}