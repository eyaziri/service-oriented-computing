export enum ReservationStatus {
  CONFIRMED = 'CONFIRMED',
  PENDING = 'PENDING',
  CANCELLED = 'CANCELLED',
  COMPLETED = 'COMPLETED',
  NO_SHOW = 'NO_SHOW'
}

export interface Reservation {
  id: number;
  reservationCode: string;
  attractionId: number;
  touristId: string;
  touristName: string;
  touristEmail: string;
  touristPhone?: string;
  touristCountry?: string;
  visitDate: string;
  visitTime: string;
  reservationTime: string;
  numberOfPeople: number;
  status: ReservationStatus;
  totalPrice: number;
  specialRequirements?: string;
  qrCodeUrl?: string;
  checkInTime?: string;
  checkOutTime?: string;
  notes?: string;
  cancellationReason?: string;
  cancelledAt?: string;
}