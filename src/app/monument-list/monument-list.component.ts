// src/app/components/monument-list/monument-list.component.ts
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { HeritageService } from '../services/heritage.service';
import { Monument } from '../models/monument.model';
import { HistoricalInfo } from '../models/historical-info.model';
import { MonumentDetailsDialogComponent } from '../monument-details-dialog/monument-details-dialog.component';
import { CompareMonumentsDialogComponent } from '../compare-monuments-dialog/compare-monuments-dialog.component';

@Component({
  selector: 'app-monument-list',
  templateUrl: './monument-list.component.html',
  styleUrls: ['./monument-list.component.scss']
})
export class MonumentListComponent implements OnInit {
  monuments: Monument[] = [];
  filteredMonuments: Monument[] = [];
  loading = false;
  searchTerm = '';
  selectedCity = '';
  cities: string[] = ['Tunis', 'El Jem', 'Sousse', 'Carthage'];

  // Tableau de comparaison
  selectedMonuments: Monument[] = [];
  
  // Statistiques
  totalMonuments = 0;
  unescoMonuments = 0;

  constructor(
    private heritageService: HeritageService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    this.loadMonuments();
  }

  loadMonuments() {
    this.loading = true;
    this.heritageService.getAllMonuments().subscribe({
      next: (monuments) => {
        // Initialiser isFavorite s'il n'existe pas
        this.monuments = monuments.map(monument => ({
          ...monument,
          isFavorite: monument.isFavorite || false
        }));
        this.filteredMonuments = [...this.monuments];
        this.updateStats();
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading monuments:', error);
        this.loading = false;
        this.showError('Erreur lors du chargement des monuments');
      }
    });
  }

  updateStats() {
    this.totalMonuments = this.monuments.length;
    this.unescoMonuments = this.monuments.filter(m => m.unescoHeritage).length;
  }

  search() {
    if (!this.searchTerm.trim() && !this.selectedCity) {
      this.filteredMonuments = [...this.monuments];
      return;
    }

    this.heritageService.searchMonuments(this.searchTerm, this.selectedCity).subscribe({
      next: (results) => {
        this.filteredMonuments = results;
      },
      error: (error) => {
        console.error('Search error:', error);
      }
    });
  }

  clearSearch() {
    this.searchTerm = '';
    this.selectedCity = '';
    this.search();
  }

  // Sélectionner/désélectionner pour comparaison
  toggleSelection(monument: Monument) {
    const index = this.selectedMonuments.findIndex(m => m.monumentId === monument.monumentId);
    if (index === -1) {
      if (this.selectedMonuments.length < 2) {
        this.selectedMonuments.push(monument);
      } else {
        this.showWarning('Maximum 2 monuments peuvent être comparés');
      }
    } else {
      this.selectedMonuments.splice(index, 1);
    }
  }

  isSelected(monument: Monument): boolean {
    return this.selectedMonuments.some(m => m.monumentId === monument.monumentId);
  }

  compareSelected() {
    if (this.selectedMonuments.length === 2) {
      const criteria = ['architecturalStyle', 'historicalPeriod', 'unescoHeritage'];
      
      this.heritageService.compareSites(
        this.selectedMonuments[0].monumentId,
        this.selectedMonuments[1].monumentId,
        criteria
      ).subscribe({
        next: (comparisonResult) => {
          this.dialog.open(CompareMonumentsDialogComponent, {
            width: '900px',
            data: {
              monumentA: this.selectedMonuments[0],
              monumentB: this.selectedMonuments[1],
              comparisonResult: comparisonResult
            }
          });
        },
        error: (error) => {
          console.error('Comparison error:', error);
          this.showError('Erreur lors de la comparaison');
        }
      });
    }
  }

  clearSelection() {
    this.selectedMonuments = [];
  }

  viewDetails(monument: Monument) {
    this.dialog.open(MonumentDetailsDialogComponent, {
      width: '800px',
      data: { monumentId: monument.monumentId }
    });
  }

  viewHistoricalInfo(monument: Monument) {
    this.dialog.open(MonumentDetailsDialogComponent, {
      width: '800px',
      data: { 
        monumentId: monument.monumentId,
        tabIndex: 1 // Ouvrir directement l'onglet historique
      }
    });
  }

  // Correction de la méthode toggleFavorite
  toggleFavorite(monument: Monument) {
    // Toggle la propriété isFavorite
    monument.isFavorite = !monument.isFavorite;
    
    // Mettre à jour la liste pour refléter le changement
    const index = this.monuments.findIndex(m => m.monumentId === monument.monumentId);
    if (index !== -1) {
      this.monuments[index] = { ...monument, isFavorite: monument.isFavorite };
      this.filteredMonuments = [...this.filteredMonuments];
    }
  }

  // Navigation
  navigateToAdmin() {
    // Navigation vers l'administration
    // this.router.navigate(['/admin/monuments']);
  }

  private showError(message: string) {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      panelClass: ['error-snackbar']
    });
  }

  private showWarning(message: string) {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      panelClass: ['warning-snackbar']
    });
  }
}