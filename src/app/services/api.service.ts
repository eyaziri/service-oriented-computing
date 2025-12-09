import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { Attraction, Category } from '../models/attraction.model';
import { Reservation, ReservationStatus } from '../models/reservation.model';
import { Review } from '../models/review.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  // ✅ CORRIGÉ: URL de base sans /attractions à la fin
  private baseUrl = 'http://localhost:8080/api';
  private attractionsUrl = `${this.baseUrl}/attractions`;

  constructor(private http: HttpClient) {}

  // === ATTRACTIONS ===

  /**
   * Récupérer toutes les attractions
   */
  getAllAttractions(): Observable<Attraction[]> {
    return this.http.get<Attraction[]>(`${this.attractionsUrl}`)
      .pipe(
        map(attractions => this.enrichAttractions(attractions)),
        catchError(() => of([]))
      );
  }

  /**
   * Récupérer les attractions avec pagination
   */
  getAttractionsPaginated(page: number = 0, size: number = 10, sortBy: string = 'name', direction: string = 'asc'): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction);

    return this.http.get<any>(`${this.attractionsUrl}/paginated`, { params })
      .pipe(
        map(response => ({
          ...response,
          content: this.enrichAttractions(response.content)
        })),
        catchError(() => of({ content: [], totalElements: 0, totalPages: 0 }))
      );
  }

  /**
   * Rechercher des attractions avec filtres
   */
  searchAttractions(searchParams?: any): Observable<any> {
    let params = new HttpParams()
      .set('page', searchParams?.page || 0)
      .set('size', searchParams?.size || 10);

    if (searchParams) {
      if (searchParams.city) params = params.set('city', searchParams.city);
      if (searchParams.category) params = params.set('category', searchParams.category);
      if (searchParams.minPrice !== undefined) params = params.set('minPrice', searchParams.minPrice);
      if (searchParams.maxPrice !== undefined) params = params.set('maxPrice', searchParams.maxPrice);
      if (searchParams.minRating) params = params.set('minRating', searchParams.minRating);
    }

    return this.http.get<any>(`${this.attractionsUrl}/search`, { params })
      .pipe(
        map(response => ({
          ...response,
          content: this.enrichAttractions(response.content)
        })),
        catchError(() => of({ content: [], totalElements: 0, totalPages: 0 }))
      );
  }

  /**
   * Recherche rapide d'attractions
   */
  quickSearchAttractions(query: string): Observable<Attraction[]> {
    return this.http.get<Attraction[]>(`${this.attractionsUrl}/search/quick`, {
      params: { query }
    }).pipe(
      map(attractions => this.enrichAttractions(attractions)),
      catchError(() => of([]))
    );
  }

  /**
   * Récupérer une attraction par ID
   */
  getAttractionById(id: number): Observable<Attraction> {
    return this.http.get<Attraction>(`${this.attractionsUrl}/${id}`)
      .pipe(
        map(attraction => this.enrichAttraction(attraction)),
        catchError(error => {
          console.error('Error loading attraction:', error);
          throw error;
        })
      );
  }

  /**
   * Récupérer les attractions par ville
   */
  getAttractionsByCity(city: string): Observable<Attraction[]> {
    return this.http.get<Attraction[]>(`${this.attractionsUrl}/city/${city}`)
      .pipe(
        map(attractions => this.enrichAttractions(attractions)),
        catchError(() => of([]))
      );
  }

  /**
   * Récupérer les attractions par ville avec pagination
   */
  getAttractionsByCityPaginated(city: string, page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<any>(`${this.attractionsUrl}/city/${city}/paginated`, { params })
      .pipe(
        map(response => ({
          ...response,
          content: this.enrichAttractions(response.content)
        })),
        catchError(() => of({ content: [], totalElements: 0, totalPages: 0 }))
      );
  }

  /**
   * Récupérer les attractions par catégorie
   */
  getAttractionsByCategory(category: Category): Observable<Attraction[]> {
    return this.http.get<Attraction[]>(`${this.attractionsUrl}/category/${category}`)
      .pipe(
        map(attractions => this.enrichAttractions(attractions)),
        catchError(() => of([]))
      );
  }

  /**
   * Récupérer les attractions en vedette
   */
  getFeaturedAttractions(): Observable<Attraction[]> {
    return this.http.get<Attraction[]>(`${this.attractionsUrl}/featured`)
      .pipe(
        map(attractions => this.enrichAttractions(attractions)),
        catchError(() => of([]))
      );
  }

  /**
   * Récupérer les attractions les mieux notées
   */
  getTopRatedAttractions(): Observable<Attraction[]> {
    return this.http.get<Attraction[]>(`${this.attractionsUrl}/top-rated`)
      .pipe(
        map(attractions => this.enrichAttractions(attractions)),
        catchError(() => of([]))
      );
  }

  /**
   * Récupérer toutes les villes disponibles
   */
  getAllCities(): Observable<string[]> {
    return this.http.get<string[]>(`${this.attractionsUrl}/cities`)
      .pipe(catchError(() => of([])));
  }

  /**
   * Récupérer toutes les catégories disponibles
   */
  getAllCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.attractionsUrl}/categories`)
      .pipe(catchError(() => of(Object.values(Category))));
  }

  /**
   * Récupérer les statistiques des attractions
   */
  getAttractionsStatistics(): Observable<any> {
    return this.http.get<any>(`${this.attractionsUrl}/statistics`)
      .pipe(catchError(() => of({})));
  }

  /**
   * Créer une nouvelle attraction
   * ✅ CORRIGÉ: URL correcte
   */
  createAttraction(attractionData: any): Observable<Attraction> {
  console.log('🌐 POST URL:', this.attractionsUrl);
  console.log('📦 Payload complet:', attractionData);
  console.log('📦 Payload JSON:', JSON.stringify(attractionData, null, 2));
  
  // Ajoutez les headers
  const headers = {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  };
  
  return this.http.post<Attraction>(this.attractionsUrl, attractionData, { 
    headers,
    observe: 'response'  // Pour voir toute la réponse
  }).pipe(
    map(response => {
      console.log('✅ Réponse réussie:', response);
      return response.body as Attraction;
    }),
    catchError((error: HttpErrorResponse) => {
      console.error('❌ ERREUR DÉTAILLÉE:');
      console.error('- Status:', error.status);
      console.error('- Status Text:', error.statusText);
      console.error('- Headers:', error.headers);
      console.error('- URL:', error.url);
      console.error('- Error body:', error.error);
      console.error('- Error message:', error.message);
      
      // Essayez de parser le corps de l'erreur
      if (error.error) {
        try {
          const errorBody = typeof error.error === 'string' ? JSON.parse(error.error) : error.error;
          console.error('- Error body parsed:', errorBody);
          
          if (errorBody.errors) {
            console.error('- Validation errors:', errorBody.errors);
          }
        } catch (e) {
          console.error('- Error body (raw):', error.error);
        }
      }
      
      throw error;
    })
  );
}

  /**
   * Mettre à jour une attraction
   */
  updateAttraction(id: number, attractionData: any): Observable<Attraction> {
    return this.http.put<Attraction>(`${this.attractionsUrl}/${id}`, attractionData);
  }

  /**
   * Supprimer une attraction
   */
  deleteAttraction(id: number): Observable<void> {
    return this.http.delete<void>(`${this.attractionsUrl}/${id}`);
  }

  /**
   * Mettre à jour le nombre de visiteurs
   */
  updateAttractionVisitors(id: number, visitorCount: number): Observable<void> {
    return this.http.patch<void>(`${this.attractionsUrl}/${id}/visitors`, null, {
      params: { visitorCount: visitorCount.toString() }
    });
  }

  // === RÉSERVATIONS ===

  /**
   * Créer une nouvelle réservation
   */
  createReservation(reservationData: any): Observable<Reservation> {
    return this.http.post<Reservation>(`${this.baseUrl}/reservations`, reservationData);
  }

  /**
   * Récupérer une réservation par ID
   */
  getReservationById(id: number): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.baseUrl}/reservations/${id}`);
  }

  /**
   * Récupérer une réservation par code
   */
  getReservationByCode(code: string): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.baseUrl}/reservations/code/${code}`);
  }

  /**
   * Récupérer les réservations d'un touriste
   */
  getReservationsByTouristId(touristId: string, page?: number, size?: number): Observable<any> {
    if (page !== undefined && size !== undefined) {
      const params = new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString());

      return this.http.get<any>(`${this.baseUrl}/reservations/tourist/${touristId}/paginated`, { params });
    }

    return this.http.get<Reservation[]>(`${this.baseUrl}/reservations/tourist/${touristId}`);
  }

  /**
   * Récupérer les réservations d'une attraction
   */
  getReservationsByAttractionId(attractionId: number, page?: number, size?: number): Observable<any> {
    if (page !== undefined && size !== undefined) {
      const params = new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString());

      return this.http.get<any>(`${this.baseUrl}/reservations/attraction/${attractionId}/paginated`, { params });
    }

    return this.http.get<Reservation[]>(`${this.baseUrl}/reservations/attraction/${attractionId}`);
  }

  /**
   * Mettre à jour le statut d'une réservation
   */
  updateReservationStatus(id: number, status: ReservationStatus): Observable<Reservation> {
    return this.http.patch<Reservation>(`${this.baseUrl}/reservations/${id}/status`, null, {
      params: { status }
    });
  }

  /**
   * Annuler une réservation
   */
  cancelReservation(id: number, reason?: string): Observable<Reservation> {
    const params = reason ? new HttpParams().set('reason', reason) : new HttpParams();
    return this.http.post<Reservation>(`${this.baseUrl}/reservations/${id}/cancel`, null, { params });
  }

  /**
   * Check-in d'une réservation
   */
  checkInReservation(id: number): Observable<Reservation> {
    return this.http.post<Reservation>(`${this.baseUrl}/reservations/${id}/checkin`, null);
  }

  /**
   * Check-out d'une réservation
   */
  checkOutReservation(id: number): Observable<Reservation> {
    return this.http.post<Reservation>(`${this.baseUrl}/reservations/${id}/checkout`, null);
  }

  /**
   * Vérifier la disponibilité
   */
  checkAvailability(attractionId: number, date: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/reservations/availability/${attractionId}`, {
      params: { date }
    });
  }

  /**
   * Récupérer les statistiques de réservation
   */
  getReservationStatistics(attractionId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/reservations/statistics/${attractionId}`);
  }

  /**
   * Récupérer les réservations du jour
   */
  getTodayReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.baseUrl}/reservations/today`);
  }

  /**
   * Récupérer les réservations à venir d'un touriste
   */
  getUpcomingReservations(touristId: string): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.baseUrl}/reservations/upcoming/${touristId}`);
  }

  // === AVIS ===

  /**
   * Créer un nouvel avis
   */
  createReview(reviewData: any): Observable<Review> {
    return this.http.post<Review>(`${this.baseUrl}/reviews`, reviewData);
  }

  /**
   * Récupérer un avis par ID
   */
  getReviewById(id: number): Observable<Review> {
    return this.http.get<Review>(`${this.baseUrl}/reviews/${id}`);
  }

  /**
   * Récupérer les avis d'une attraction
   */
  getAttractionReviews(attractionId: number, page?: number, size?: number, sortBy?: string): Observable<any> {
    let url = `${this.baseUrl}/reviews/attraction/${attractionId}`;
    
    if (sortBy) {
      url = `${this.baseUrl}/reviews/attraction/${attractionId}/sorted`;
      const params = new HttpParams()
        .set('sortBy', sortBy)
        .set('page', page?.toString() || '0')
        .set('size', size?.toString() || '10')
        .set('direction', 'desc');

      return this.http.get<any>(url, { params });
    } else if (page !== undefined && size !== undefined) {
      const params = new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString());

      return this.http.get<any>(`${this.baseUrl}/reviews/attraction/${attractionId}/paginated`, { params });
    }

    return this.http.get<Review[]>(url);
  }

  /**
   * Récupérer les avis d'un touriste
   */
  getReviewsByTouristId(touristId: string, page?: number, size?: number): Observable<any> {
    if (page !== undefined && size !== undefined) {
      const params = new HttpParams()
        .set('page', page.toString())
        .set('size', size.toString());

      return this.http.get<any>(`${this.baseUrl}/reviews/tourist/${touristId}/paginated`, { params });
    }

    return this.http.get<Review[]>(`${this.baseUrl}/reviews/tourist/${touristId}`);
  }

  /**
   * Récupérer les avis vérifiés d'une attraction
   */
  getVerifiedReviews(attractionId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.baseUrl}/reviews/attraction/${attractionId}/verified`);
  }

  /**
   * Mettre à jour un avis
   */
  updateReview(id: number, reviewData: any): Observable<Review> {
    return this.http.put<Review>(`${this.baseUrl}/reviews/${id}`, reviewData);
  }

  /**
   * Supprimer un avis
   */
  deleteReview(id: number, touristId: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/reviews/${id}`, {
      params: { touristId }
    });
  }

  /**
   * Marquer un avis comme utile
   */
  markReviewAsHelpful(id: number): Observable<Review> {
    return this.http.post<Review>(`${this.baseUrl}/reviews/${id}/helpful`, null);
  }

  /**
   * Ajouter une réponse à un avis
   */
  addReplyToReview(id: number, reply: string): Observable<Review> {
    return this.http.post<Review>(`${this.baseUrl}/reviews/${id}/reply`, null, {
      params: { reply }
    });
  }

  /**
   * Récupérer les statistiques d'avis
   */
  getReviewStatistics(attractionId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/reviews/statistics/${attractionId}`);
  }

  /**
   * Récupérer le résumé des avis
   */
  getReviewSummary(attractionId: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/reviews/summary/${attractionId}`);
  }

  /**
   * Récupérer les avis récents
   */
  getRecentReviews(limit: number = 10): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.baseUrl}/reviews/recent`, {
      params: { limit: limit.toString() }
    });
  }

  /**
   * Récupérer les attractions les mieux notées
   */
  getTopRatedAttractionsWithReviews(limit: number = 5): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/reviews/top-rated`, {
      params: { limit: limit.toString() }
    });
  }

  /**
   * Rechercher dans les avis
   */
  searchReviews(query: string): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.baseUrl}/reviews/search`, {
      params: { query }
    });
  }

  /**
   * Vérifier un avis
   */
  verifyReview(id: number, verified: boolean = true): Observable<Review> {
    return this.http.patch<Review>(`${this.baseUrl}/reviews/${id}/verify`, null, {
      params: { verified: verified.toString() }
    });
  }

  // === HELPER METHODS ===

  /**
   * Enrichir une attraction avec des données calculées
   */
  private enrichAttraction(attraction: Attraction): Attraction {
    return {
      ...attraction,
      isOpen: this.isAttractionOpen(attraction),
      availableSpots: attraction.maxCapacity ? 
        Math.max(0, attraction.maxCapacity - (attraction.currentVisitors || 0)) : undefined,
      occupancyRate: attraction.maxCapacity && attraction.maxCapacity > 0 ? 
        ((attraction.currentVisitors || 0) / attraction.maxCapacity) * 100 : 0
    };
  }

  /**
   * Enrichir un tableau d'attractions
   */
  private enrichAttractions(attractions: Attraction[]): Attraction[] {
    return attractions.map(attraction => this.enrichAttraction(attraction));
  }

  /**
   * Vérifier si une attraction est ouverte
   */
  private isAttractionOpen(attraction: Attraction): boolean {
    if (!attraction.openingHours || !attraction.closingHours) return true;
    
    const now = new Date();
    const currentTime = now.getHours() * 100 + now.getMinutes();
    const opening = this.timeToMinutes(attraction.openingHours);
    const closing = this.timeToMinutes(attraction.closingHours);
    
    return currentTime >= opening && currentTime <= closing;
  }

  /**
   * Convertir le temps en minutes
   */
  private timeToMinutes(time: string): number {
    if (!time) return 0;
    const [hours, minutes] = time.split(':').map(Number);
    return hours * 100 + (minutes || 0);
  }

  /**
   * Afficher le statut de réservation
   */
  getReservationStatusDisplay(status: ReservationStatus): string {
    const statusMap: { [key in ReservationStatus]: string } = {
      [ReservationStatus.CONFIRMED]: 'Confirmée',
      [ReservationStatus.PENDING]: 'En attente',
      [ReservationStatus.CANCELLED]: 'Annulée',
      [ReservationStatus.COMPLETED]: 'Terminée',
      [ReservationStatus.NO_SHOW]: 'Non présent'
    };
    return statusMap[status] || status;
  }

  /**
   * Afficher le nom de la catégorie
   */
  getCategoryDisplay(category: Category): string {
    const categoryMap: { [key in Category]: string } = {
      [Category.MUSEUM]: 'Musée',
      [Category.MONUMENT]: 'Monument',
      [Category.PARK]: 'Parc',
      [Category.RESTAURANT]: 'Restaurant',
      [Category.BEACH]: 'Plage',
      [Category.SHOPPING]: 'Shopping',
      [Category.RELIGIOUS]: 'Religieux',
      [Category.HISTORICAL]: 'Historique',
      [Category.ENTERTAINMENT]: 'Divertissement',
      [Category.OTHER]: 'Autre'
    };
    return categoryMap[category] || category;
  }

  /**
   * Formater la date pour l'API
   */
  formatDateForApi(date: Date): string {
    return date.toISOString().split('T')[0];
  }

  /**
   * Générer un ID de touriste temporaire
   */
  generateTempTouristId(): string {
    return `temp_tourist_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  /**
   * Simuler un utilisateur connecté (à remplacer par votre authentification)
   */
  getCurrentUser(): any {
    return {
      id: 'tourist-123',
      name: 'Jean Dupont',
      email: 'jean.dupont@example.com',
      phone: '0612345678',
      country: 'France'
    };
  }
}