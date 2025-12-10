import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../../services/api.service';
import { Attraction, Category } from '../../models/attraction.model';
import { Subscription } from 'rxjs';
import { AlertService, Alert } from '../../services/alert.service';

@Component({
  selector: 'app-attractions-search',
  templateUrl: './attractions-search.component.html',
  styleUrls: ['./attractions-search.component.scss']
})
export class AttractionsSearchComponent implements OnInit, OnDestroy {
  searchForm: FormGroup;
  attractions: Attraction[] = [];
  filteredAttractions: Attraction[] = [];
  isLoading = false;
  categories = Object.values(Category);
  searchParams: any = {};

  // Filtres avancés
  showAdvancedFilters = false;
  priceRange = { min: 0, max: 100 };
  selectedRating = 0;

  // Alertes
  alerts: Alert[] = [];
  relevantAlerts: Alert[] = [];
  showAlertPanel = false;
  private alertSubscription: Subscription | null = null;
  private activeAlertCheckInterval: any;

  // Pagination
  currentPage = 1;
  itemsPerPage = 9;
  totalItems = 0;

  // Tri
  sortOptions = [
    { value: 'name', label: 'Nom (A-Z)' },
    { value: 'name_desc', label: 'Nom (Z-A)' },
    { value: 'rating_desc', label: 'Meilleures notes' },
    { value: 'price_asc', label: 'Prix croissant' },
    { value: 'price_desc', label: 'Prix décroissant' }
  ];
  selectedSort = 'rating_desc';

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private router: Router,
    private alertService: AlertService,
    private snackBar: MatSnackBar
  ) {
    this.searchForm = this.fb.group({
      search: [''],
      category: [''],
      city: [''],
      minPrice: [0],
      maxPrice: [100],
      minRating: [0]
    });
  }

  ngOnInit(): void {
    this.loadAttractions();
    this.loadFeaturedAttractions();
    
    // ⭐ AJOUT CRITIQUE : Initialiser le système d'alertes
    this.initializeAlertSystem();
  }

  ngOnDestroy(): void {
    if (this.alertSubscription) {
      this.alertSubscription.unsubscribe();
    }
    
    if (this.activeAlertCheckInterval) {
      clearInterval(this.activeAlertCheckInterval);
    }
    
    this.alertService.disconnect();
  }

  // Méthodes pour le système d'alertes
  initializeAlertSystem(): void {
    // Demander la permission pour les notifications
    if ('Notification' in window && Notification.permission === 'default') {
      Notification.requestPermission();
    }
    
    // S'abonner aux nouvelles alertes
    this.alertSubscription = this.alertService.alertReceived$.subscribe(alert => {
      this.checkRelevantAlerts(alert);
    });
    
    // Charger les alertes existantes
    this.loadExistingAlerts();
    
    // Vérifier périodiquement les alertes pertinentes
    this.activeAlertCheckInterval = setInterval(() => {
      this.checkRelevantAlertsForCurrentFilters();
    }, 30000);
  }

  loadExistingAlerts(): void {
    this.alertService.getActiveAlerts().subscribe({
      next: (response) => {
        if (response.alerts) {
          this.alerts = response.alerts.map((alert: any) => ({
            id: alert.alertId || alert.id,
            type: alert.type,
            location: alert.location,
            message: alert.message,
            severity: alert.severity,
            timestamp: alert.timestamp,
            status: alert.status || 'ACTIVE'
          }));
          
          this.checkRelevantAlertsForCurrentFilters();
        }
      },
      error: (error) => {
        console.error('Erreur chargement alertes:', error);
      }
    });
  }

  checkRelevantAlerts(alert: Alert): void {
    const isRelevant = this.isAlertRelevant(alert);
    
    if (isRelevant) {
      // Ajouter aux alertes pertinentes (éviter les doublons)
      const exists = this.relevantAlerts.some(a => a.id === alert.id);
      if (!exists) {
        this.relevantAlerts.unshift(alert);
        
        // Limiter à 5 alertes pertinentes
        if (this.relevantAlerts.length > 5) {
          this.relevantAlerts.pop();
        }
        
        // Afficher une notification pour les alertes importantes
        if (alert.severity >= 3) {
          this.showAlertToast(alert);
        }
      }
    }
  }

  checkRelevantAlertsForCurrentFilters(): void {
    const cityFilter = this.cityControl.value;
    const categoryFilter = this.categoryControl.value;
    
    if (cityFilter || categoryFilter) {
      this.relevantAlerts = this.alerts.filter(alert => 
        this.isAlertRelevant(alert)
      );
    } else {
      this.relevantAlerts = [];
    }
  }

  isAlertRelevant(alert: Alert): boolean {
    const cityFilter = this.cityControl.value?.toLowerCase();
    const categoryFilter = this.categoryControl.value;
    
    if (cityFilter && alert.location.toLowerCase().includes(cityFilter)) {
      return true;
    }
    
    if (categoryFilter && alert.type.toLowerCase() === categoryFilter.toLowerCase()) {
      return true;
    }
    
    return false;
  }

  showAlertToast(alert: Alert): void {
    const message = `${alert.type}: ${alert.message} (${alert.location})`;
    const action = alert.severity >= 4 ? 'Détails' : 'OK';
    
    const snackBarRef = this.snackBar.open(message, action, {
      duration: alert.severity >= 4 ? 10000 : 5000,
      panelClass: [`alert-severity-${alert.severity}`, 'alert-snackbar'],
      horizontalPosition: 'end',
      verticalPosition: 'top'
    });
    
    snackBarRef.onAction().subscribe(() => {
      this.showAlertPanel = true;
    });
  }

  // Getters pour les contrôles du formulaire
  get searchControl(): FormControl {
    return this.searchForm.get('search') as FormControl;
  }

  get categoryControl(): FormControl {
    return this.searchForm.get('category') as FormControl;
  }

  get cityControl(): FormControl {
    return this.searchForm.get('city') as FormControl;
  }

  get minPriceControl(): FormControl {
    return this.searchForm.get('minPrice') as FormControl;
  }

  get maxPriceControl(): FormControl {
    return this.searchForm.get('maxPrice') as FormControl;
  }

  loadAttractions(): void {
    this.isLoading = true;
    
    this.apiService.searchAttractions(this.buildSearchParams()).subscribe({
      next: (response) => {
        this.attractions = response.content || [];
        this.totalItems = response.totalElements || 0;
        this.applyFiltersAndSort();
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading attractions:', error);
        this.attractions = [];
        this.totalItems = 0;
        this.filteredAttractions = [];
        this.isLoading = false;
      }
    });
  }

  buildSearchParams(): any {
    const formValue = this.searchForm.value;
    const params: any = {
      page: this.currentPage - 1,
      size: this.itemsPerPage
    };

    if (formValue.search) {
      return { query: formValue.search, ...params };
    }
    
    if (formValue.category) params.category = formValue.category;
    if (formValue.city) params.city = formValue.city;
    if (formValue.minPrice) params.minPrice = formValue.minPrice;
    if (formValue.maxPrice) params.maxPrice = formValue.maxPrice;
    if (formValue.minRating) params.minRating = formValue.minRating;

    return params;
  }

  loadFeaturedAttractions(): void {
    this.apiService.getFeaturedAttractions().subscribe(attractions => {
      // Traitement des attractions en vedette
    });
  }

  onSearch(): void {
    this.currentPage = 1;
    this.searchParams = this.buildSearchParams();
    this.loadAttractions();
    
    // Vérifier les alertes pertinentes après une recherche
    this.checkRelevantAlertsForCurrentFilters();
  }

  applyFiltersAndSort(): void {
    let filtered = [...this.attractions];

    filtered = this.sortAttractions(filtered);

    if (this.selectedRating > 0) {
      filtered = filtered.filter(a => a.rating >= this.selectedRating);
    }

    if (this.priceRange.min > 0 || this.priceRange.max < 100) {
      filtered = filtered.filter(a => {
        const price = a.entryPrice || 0;
        return price >= this.priceRange.min && price <= this.priceRange.max;
      });
    }

    this.totalItems = filtered.length;
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    this.filteredAttractions = filtered.slice(startIndex, startIndex + this.itemsPerPage);
  }

  sortAttractions(attractions: Attraction[]): Attraction[] {
    switch (this.selectedSort) {
      case 'name':
        return attractions.sort((a, b) => a.name.localeCompare(b.name));
      case 'name_desc':
        return attractions.sort((a, b) => b.name.localeCompare(a.name));
      case 'rating_desc':
        return attractions.sort((a, b) => b.rating - a.rating);
      case 'price_asc':
        return attractions.sort((a, b) => (a.entryPrice || 0) - (b.entryPrice || 0));
      case 'price_desc':
        return attractions.sort((a, b) => (b.entryPrice || 0) - (a.entryPrice || 0));
      default:
        return attractions;
    }
  }

  onSortChange(): void {
    this.applyFiltersAndSort();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.applyFiltersAndSort();
  }

  onClearFilters(): void {
    this.searchForm.reset({
      search: '',
      category: '',
      city: '',
      minPrice: 0,
      maxPrice: 100,
      minRating: 0
    });
    this.selectedRating = 0;
    this.priceRange = { min: 0, max: 100 };
    this.selectedSort = 'rating_desc';
    this.onSearch();
  }

  viewAttractionDetails(attractionId: number): void {
    this.router.navigate(['/attractions', attractionId]);
  }

  toggleAdvancedFilters(): void {
    this.showAdvancedFilters = !this.showAdvancedFilters;
  }

  getCategoryDisplay(category: Category): string {
    return this.apiService.getCategoryDisplay(category);
  }

  getStarsArray(rating: number): number[] {
    return Array(5).fill(0).map((_, i) => i < Math.floor(rating) ? 1 : 0);
  }

  get totalPages(): number {
    return Math.ceil(this.totalItems / this.itemsPerPage);
  }

  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxVisible = 5;
    let start = Math.max(1, this.currentPage - Math.floor(maxVisible / 2));
    let end = Math.min(this.totalPages, start + maxVisible - 1);
    
    if (end - start + 1 < maxVisible) {
      start = Math.max(1, end - maxVisible + 1);
    }
    
    for (let i = start; i <= end; i++) {
      pages.push(i);
    }
    
    return pages;
  }

  formatPrice(value: number): string {
    return `${value}€`;
  }

  get formatPriceFunction(): (value: number) => string {
    return (value: number) => this.formatPrice(value);
  }
}