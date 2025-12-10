import { Component, OnInit } from '@angular/core';
import { CulturalHeritageService,Monument, HistoricalInfo, TouristStats, ComparisonResult } from '../services/culturalHeritage.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-cultural-heritage',
  templateUrl: './cultural-heritage.component.html',
  styleUrls: ['./cultural-heritage.component.scss']
})
export class CulturalHeritageComponent implements OnInit {
  // Onglets
  activeTab: 'monuments' | 'historical' | 'stats' | 'comparison' = 'monuments';
  
  // Données
  monuments: Monument[] = [];
  selectedMonument: Monument | null = null;
  historicalInfo: HistoricalInfo | null = null;
  touristStats: TouristStats | null = null;
  comparisonResult: ComparisonResult | null = null;
  
  // Formulaires
  monumentId: string = 'M001';
  region: string = 'Tunis';
  year: number = 2023;
  siteAId: string = 'M001';
  siteBId: string = 'M002';
  criteria: string[] = ['age', 'unesco'];
  
  // États
  isLoading = false;
  errorMessage: string = '';

  constructor(
    private heritageService: CulturalHeritageService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadSampleMonuments();
  }

  // Charger les monuments de démo
  loadSampleMonuments(): void {
    this.monuments = this.heritageService.getSampleMonuments();
  }

  // 1. Récupérer les informations historiques
  getHistoricalDescription(): void {
    if (!this.monumentId) {
      this.showError('Veuillez entrer un ID de monument');
      return;
    }

    this.isLoading = true;
    this.heritageService.getHistoricalDescription(this.monumentId)
      .subscribe({
        next: (info) => {
          this.historicalInfo = info;
          this.activeTab = 'historical';
          this.showSuccess('Informations historiques récupérées avec succès');
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = error.message || 'Erreur lors de la récupération';
          this.showError(this.errorMessage);
          this.isLoading = false;
        }
      });
  }

  // Méthode pour obtenir le maximum de visiteurs pour le graphique
getMaxVisitors(): number {
  if (!this.touristStats || this.touristStats.monthlyStats.length === 0) {
    return 100;
  }
  return Math.max(...this.touristStats.monthlyStats.map(m => m.visitors));
}

// Méthodes pour la comparaison
getComparisonKeys(): string[] {
  if (!this.comparisonResult) return [];
  return Object.keys(this.comparisonResult.comparisons || {})
    .filter(key => !['siteA', 'siteB', 'comparisonDate', 'comparisonType'].includes(key));
}

formatCriterion(key: string): string {
  const map: {[key: string]: string} = {
    'ageA': 'Âge (Site A)',
    'ageB': 'Âge (Site B)',
    'ageComparison': 'Comparaison d\'âge',
    'unescoA': 'Classement UNESCO (A)',
    'unescoB': 'Classement UNESCO (B)',
    'unescoComparison': 'Comparaison UNESCO',
    'cityComparison': 'Comparaison de villes'
  };
  return map[key] || key;
}

getSiteAValue(key: string): string {
  if (!this.comparisonResult) return '';
  const map: {[key: string]: string} = {
    'ageA': this.comparisonResult.comparisons['ageA'],
    'unescoA': this.comparisonResult.comparisons['unescoA'],
    'cityA': this.comparisonResult.comparisons['siteA_City']
  };
  return map[key] || '';
}

getSiteBValue(key: string): string {
  if (!this.comparisonResult) return '';
  const map: {[key: string]: string} = {
    'ageB': this.comparisonResult.comparisons['ageB'],
    'unescoB': this.comparisonResult.comparisons['unescoB'],
    'cityB': this.comparisonResult.comparisons['siteB_City']
  };
  return map[key] || '';
}

  // 2. Récupérer les statistiques touristiques
  getAnnualTouristStats(): void {
    this.isLoading = true;
    this.heritageService.getAnnualTouristStats(this.region, this.year)
      .subscribe({
        next: (stats) => {
          this.touristStats = stats;
          this.activeTab = 'stats';
          this.showSuccess('Statistiques touristiques récupérées avec succès');
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = error.message || 'Erreur lors de la récupération';
          this.showError(this.errorMessage);
          this.isLoading = false;
        }
      });
  }

  // 3. Comparer deux sites
  compareSites(): void {
    if (!this.siteAId || !this.siteBId) {
      this.showError('Veuillez sélectionner deux sites à comparer');
      return;
    }

    this.isLoading = true;
    this.heritageService.compareHeritageSites(this.siteAId, this.siteBId, this.criteria)
      .subscribe({
        next: (result) => {
          this.comparisonResult = result;
          this.activeTab = 'comparison';
          this.showSuccess('Comparaison effectuée avec succès');
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = error.message || 'Erreur lors de la comparaison';
          this.showError(this.errorMessage);
          this.isLoading = false;
        }
      });
  }

  // Sélectionner un monument
  selectMonument(monument: Monument): void {
    this.selectedMonument = monument;
    this.monumentId = monument.monumentId;
  }

  // Basculer un critère de comparaison
  toggleCriteria(criterion: string): void {
    const index = this.criteria.indexOf(criterion);
    if (index > -1) {
      this.criteria.splice(index, 1);
    } else {
      this.criteria.push(criterion);
    }
  }

  // Messages
  private showSuccess(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 3000,
      panelClass: ['success-snackbar']
    });
  }

  private showError(message: string): void {
    this.snackBar.open(message, 'Fermer', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }
}