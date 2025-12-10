// src/app/components/monument-details-dialog/monument-details-dialog.component.ts
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { HeritageService } from '../services/heritage.service';
import { Monument,HistoricalInfo, TouristStats, MonthlyStat } from '../models/restoration-history.model';

@Component({
  selector: 'app-monument-details-dialog',
  templateUrl: './monument-details-dialog.component.html',
  styleUrls: ['./monument-details-dialog.component.scss']
})
export class MonumentDetailsDialogComponent implements OnInit {
  monument: Monument | null = null;
  historicalInfo: HistoricalInfo | null = null;
  touristStats: TouristStats | null = null;
  
  loading = true;
  activeTabIndex = 0;
  
  // Pour la comparaison
  compareMonumentId = '';
  comparisonResult: any = null;
  comparing = false;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialogRef: MatDialogRef<MonumentDetailsDialogComponent>,
    private heritageService: HeritageService
  ) {
    this.activeTabIndex = data.tabIndex || 0;
  }

  ngOnInit() {
    this.loadMonumentData();
  }

  loadMonumentData() {
    this.loading = true;
    
    // Charger le monument
    this.heritageService.getMonumentById(this.data.monumentId).subscribe({
      next: (monument) => {
        if (monument) {
          this.monument = monument;
          
          // Charger les informations historiques
          this.heritageService.getHistoricalInfo(monument.monumentId).subscribe({
            next: (info) => {
              this.historicalInfo = info;
              this.loading = false;
            },
            error: (error) => {
              console.error('Error loading historical info:', error);
              this.historicalInfo = this.getDefaultHistoricalInfo();
              this.loading = false;
            }
          });
          
          // Charger les statistiques touristiques (si disponible)
          this.loadTouristStats();
        } else {
          // Monument non trouvé
          this.monument = this.getDefaultMonument();
          this.loading = false;
        }
      },
      error: (error) => {
        console.error('Error loading monument:', error);
        this.monument = this.getDefaultMonument();
        this.loading = false;
      }
    });
  }

  loadTouristStats() {
    // Adapter la région selon la ville du monument
    if (this.monument) {
      const region = this.getRegionFromCity(this.monument.city);
      
      if (region) {
        this.heritageService.getTouristStats(region, new Date().getFullYear() - 1).subscribe({
          next: (stats) => {
            this.touristStats = stats;
          },
          error: (error) => {
            console.error('Error loading tourist stats:', error);
            this.touristStats = this.getMockTouristStats();
          }
        });
      } else {
        this.touristStats = this.getMockTouristStats();
      }
    }
  }

  getRegionFromCity(city: string): string {
    // Logique simple pour mapper les villes aux régions
    const regionMap: { [key: string]: string } = {
      'Tunis': 'Tunis',
      'El Jem': 'Mahdia',
      'Sousse': 'Sousse',
      'Carthage': 'Tunis',
      'Mahdia': 'Mahdia',
      'Monastir': 'Monastir'
    };
    
    return regionMap[city] || city;
  }

  onTabChange(event: MatTabChangeEvent) {
    this.activeTabIndex = event.index;
  }

  // Fonction pour comparer avec un autre monument
  compareWithOther() {
    if (!this.compareMonumentId.trim() || !this.monument) return;
    
    this.comparing = true;
    const criteria = ['historicalSignificance', 'culturalImportance', 'officialClassification'];
    
    this.heritageService.compareSites(
      this.monument.monumentId,
      this.compareMonumentId,
      criteria
    ).subscribe({
      next: (result) => {
        this.comparisonResult = result;
        this.comparing = false;
      },
      error: (error) => {
        console.error('Comparison error:', error);
        this.comparing = false;
      }
    });
  }

  // Méthode pour calculer la largeur des barres du graphique
  getBarWidth(visitors: number, monthlyStats: MonthlyStat[]): number {
    if (!monthlyStats || monthlyStats.length === 0) {
      return 0;
    }
    
    const maxVisitors = Math.max(...monthlyStats.map(stat => stat.visitors));
    return maxVisitors > 0 ? (visitors / maxVisitors) * 100 : 0;
  }

  // Méthode pour obtenir les clés de comparaison
  getComparisonKeys(): string[] {
    if (!this.comparisonResult?.comparisons) return [];
    return Object.keys(this.comparisonResult.comparisons);
  }

  close() {
    this.dialogRef.close();
  }

  // Méthodes utilitaires pour les données par défaut
  private getDefaultMonument(): Monument {
    return {
      monumentId: this.data.monumentId || 'UNKNOWN',
      name: 'Monument non trouvé',
      city: 'Inconnue',
      yearBuilt: 0,
      architecturalStyle: 'Inconnu',
      unescoHeritage: false,
      historicalPeriod: 'Inconnue'
    };
  }

  private getDefaultHistoricalInfo(): HistoricalInfo {
    return {
      monumentId: this.data.monumentId || 'UNKNOWN',
      description: 'Aucune description disponible.',
      historicalSignificance: 'Non spécifiée',
      restorationHistory: [],
      culturalImportance: 'Non spécifiée',
      officialClassification: 'Non classé'
    };
  }

  private getMockTouristStats(): TouristStats {
    return {
      region: this.monument?.city || 'Inconnue',
      year: new Date().getFullYear() - 1,
      totalVisitors: Math.floor(Math.random() * 100000) + 50000,
      internationalVisitors: Math.floor(Math.random() * 50000) + 10000,
      growthRate: (Math.random() * 0.2) + 0.05,
      monthlyStats: [
        { month: 'Janvier', visitors: Math.floor(Math.random() * 10000) + 2000 },
        { month: 'Février', visitors: Math.floor(Math.random() * 12000) + 3000 },
        { month: 'Mars', visitors: Math.floor(Math.random() * 15000) + 4000 },
        { month: 'Avril', visitors: Math.floor(Math.random() * 18000) + 5000 },
        { month: 'Mai', visitors: Math.floor(Math.random() * 20000) + 6000 },
        { month: 'Juin', visitors: Math.floor(Math.random() * 22000) + 7000 }
      ]
    };
  }
}