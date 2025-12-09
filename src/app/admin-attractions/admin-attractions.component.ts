import { Component, OnInit, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ApiService } from '../services/api.service';
import { Attraction, Category } from '../models/attraction.model';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { AttractionFormDialogComponent } from '../attraction-form-dialog/attraction-form-dialog.component';
import { NotificationDialogComponent } from '../notification-dialog/notification-dialog.component';
import { NotificationService } from '../services/notification.service';

@Component({
  selector: 'app-admin-attractions',
  templateUrl: './admin-attractions.component.html',
  styleUrls: ['./admin-attractions.component.scss']
})
export class AdminAttractionsComponent implements OnInit {
  displayedColumns: string[] = [
    'id', 
    'name', 
    'category', 
    'city', 
    'entryPrice', 
    'currentVisitors', 
    'rating', 
    'isActive',
    'isFeatured',
    'actions'
  ];
  
  dataSource = new MatTableDataSource<Attraction>([]);
  loading = false;
  totalItems = 0;
  pageSize = 10;
  pageIndex = 0;
  
  categories = Object.values(Category);
  cities: string[] = [];
  
  // Filtres
  selectedCategory: string = '';
  selectedCity: string = '';
  searchQuery: string = '';
  showActiveOnly = true;
  
  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private apiService: ApiService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private notificationService: NotificationService, // Ajoutez ceci

  ) {}

  ngOnInit() {
    this.loadCities();
    this.loadAttractions();
  }

  openNotificationDialog(attraction: Attraction) {
  const dialogRef = this.dialog.open(NotificationDialogComponent, {
    width: '500px',
    data: {
      attractionId: attraction.id,
      attractionName: attraction.name,
      attractionLocation: attraction.location?.city || attraction.city
    }
  });

  dialogRef.afterClosed().subscribe(result => {
    if (result) {
      this.snackBar.open('Notification créée avec succès', 'Fermer', {
        duration: 2000
      });
    }
  });
}

  ngAfterViewInit() {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  // Fonctions utilitaires pour le style
getCategoryColor(category: Category): string {
  const colorMap: { [key in Category]: string } = {
    [Category.MUSEUM]: '#2196f3',
    [Category.MONUMENT]: '#4caf50',
    [Category.PARK]: '#8bc34a',
    [Category.RESTAURANT]: '#ff9800',
    [Category.BEACH]: '#00bcd4',
    [Category.SHOPPING]: '#9c27b0',
    [Category.RELIGIOUS]: '#795548',
    [Category.HISTORICAL]: '#607d8b',
    [Category.ENTERTAINMENT]: '#e91e63',
    [Category.OTHER]: '#9e9e9e'
  };
  return colorMap[category] || '#9e9e9e';
}

// Ajoutez ces méthodes
getTotalAttractions(): number {
  return this.totalItems; // Utilisez déjà cette propriété
}

getActiveAttractionsCount(): number {
  return this.dataSource.data.filter(a => a.isActive).length;
}

getFeaturedAttractionsCount(): number {
  return this.dataSource.data.filter(a => a.isFeatured).length;
}

getTotalVisitors(): number {
  return this.dataSource.data.reduce((sum, a) => sum + (a.currentVisitors || 0), 0);
}

getOccupancyColor(attraction: Attraction): string {
  if (!attraction.maxCapacity || attraction.maxCapacity === 0) return 'primary';
  
  const occupancy = (attraction.currentVisitors || 0) / attraction.maxCapacity;
  
  if (occupancy < 0.5) return 'primary';  // Vert
  if (occupancy < 0.8) return 'accent';   // Orange
  return 'warn';                          // Rouge
}

  loadAttractions() {
    this.loading = true;
    
    const params: any = {
      page: this.pageIndex,
      size: this.pageSize,
      sortBy: this.sort?.active || 'name',
      direction: this.sort?.direction || 'asc'
    };

    // Appliquer les filtres
    if (this.selectedCategory) {
      params.category = this.selectedCategory;
    }
    
    if (this.selectedCity) {
      params.city = this.selectedCity;
    }
    
    if (this.searchQuery) {
      // Recherche rapide
      this.apiService.quickSearchAttractions(this.searchQuery).subscribe({
        next: (attractions) => {
          this.dataSource.data = attractions;
          this.totalItems = attractions.length;
          this.loading = false;
        },
        error: (error) => {
          console.error('Search error:', error);
          this.loading = false;
        }
      });
      return;
    }

    // Chargement normal avec filtres
    this.apiService.searchAttractions(params).subscribe({
      next: (response) => {
        this.dataSource.data = response.content || [];
        this.totalItems = response.totalElements || 0;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading attractions:', error);
        this.loading = false;
        this.snackBar.open('Erreur lors du chargement des attractions', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  loadCities() {
    this.apiService.getAllCities().subscribe({
      next: (cities) => {
        this.cities = cities;
      },
      error: (error) => {
        console.error('Error loading cities:', error);
      }
    });
  }

  onPageChange(event: any) {
    this.pageIndex = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadAttractions();
  }

  onSortChange(event: any) {
    this.loadAttractions();
  }

  applyFilters() {
    this.pageIndex = 0;
    this.loadAttractions();
  }

  clearFilters() {
    this.selectedCategory = '';
    this.selectedCity = '';
    this.searchQuery = '';
    this.showActiveOnly = true;
    this.applyFilters();
  }

  // CRUD Operations
  openCreateDialog() {
    const dialogRef = this.dialog.open(AttractionFormDialogComponent, {
      width: '800px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: { mode: 'create' }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadAttractions();
        this.snackBar.open('Attraction créée avec succès', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  openEditDialog(attraction: Attraction) {
    const dialogRef = this.dialog.open(AttractionFormDialogComponent, {
      width: '800px',
      maxWidth: '90vw',
      maxHeight: '90vh',
      data: { mode: 'edit', attraction }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadAttractions();
        this.snackBar.open('Attraction mise à jour avec succès', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  confirmDelete(attraction: Attraction) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Supprimer l\'attraction',
        message: `Êtes-vous sûr de vouloir supprimer "${attraction.name}" ? Cette action est irréversible.`,
        confirmText: 'Supprimer',
        cancelText: 'Annuler',
        color: 'warn'
      }
    });

    dialogRef.afterClosed().subscribe(confirmed => {
      if (confirmed) {
        this.deleteAttraction(attraction.id);
      }
    });
  }

  deleteAttraction(id: number) {
    this.loading = true;
    this.apiService.deleteAttraction(id).subscribe({
      next: () => {
        this.loadAttractions();
        this.snackBar.open('Attraction supprimée avec succès', 'Fermer', {
          duration: 3000
        });
      },
      error: (error) => {
        console.error('Error deleting attraction:', error);
        this.loading = false;
        this.snackBar.open('Erreur lors de la suppression', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  toggleActiveStatus(attraction: Attraction) {
    const updatedData = { ...attraction, isActive: !attraction.isActive };
    
    this.apiService.updateAttraction(attraction.id, updatedData).subscribe({
      next: () => {
        this.loadAttractions();
        this.snackBar.open(`Attraction ${updatedData.isActive ? 'activée' : 'désactivée'}`, 'Fermer', {
          duration: 2000
        });
      },
      error: (error) => {
        console.error('Error updating status:', error);
        this.snackBar.open('Erreur lors de la mise à jour', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  toggleFeaturedStatus(attraction: Attraction) {
    const updatedData = { ...attraction, isFeatured: !attraction.isFeatured };
    
    this.apiService.updateAttraction(attraction.id, updatedData).subscribe({
      next: () => {
        this.loadAttractions();
        this.snackBar.open(`Attraction ${updatedData.isFeatured ? 'mise en vedette' : 'retirée des vedettes'}`, 'Fermer', {
          duration: 2000
        });
      },
      error: (error) => {
        console.error('Error updating featured status:', error);
        this.snackBar.open('Erreur lors de la mise à jour', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  updateVisitors(attraction: Attraction) {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: {
        title: 'Mettre à jour les visiteurs',
        message: `Entrez le nouveau nombre de visiteurs pour "${attraction.name}":`,
        input: true,
        inputLabel: 'Nombre de visiteurs',
        inputValue: attraction.currentVisitors,
        inputType: 'number',
        confirmText: 'Mettre à jour',
        cancelText: 'Annuler'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result !== undefined && result !== null) {
        const visitorCount = Number(result);
        this.apiService.updateAttractionVisitors(attraction.id, visitorCount).subscribe({
          next: () => {
            this.loadAttractions();
            this.snackBar.open('Nombre de visiteurs mis à jour', 'Fermer', {
              duration: 2000
            });
          },
          error: (error) => {
            console.error('Error updating visitors:', error);
            this.snackBar.open('Erreur lors de la mise à jour', 'Fermer', {
              duration: 3000
            });
          }
        });
      }
    });
  }

  exportToCSV() {
    // Implémentez l'export CSV ici
    this.snackBar.open('Fonctionnalité d\'export à implémenter', 'Fermer', {
      duration: 2000
    });
  }

  getCategoryDisplay(category: Category): string {
    return this.apiService.getCategoryDisplay(category);
  }

  getRowClass(attraction: Attraction): string {
    if (!attraction.isActive) return 'inactive-row';
    if (attraction.isFeatured) return 'featured-row';
    return '';
  }
}