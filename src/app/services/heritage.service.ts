// src/app/services/heritage.service.ts
import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';
import { CulturalHeritageService } from './cultural-heritage.service';
import { Monument } from '../models/monument.model';
import { HistoricalInfo } from '../models/historical-info.model';
import { TouristStats } from '../models/tourist-stats.model';

// Données mockées UNIQUEMENT pour les monuments (liste)
const MOCK_MONUMENTS: Monument[] = [
  {
    monumentId: 'M001',
    name: 'Musée National du Bardo',
    city: 'Tunis',
    yearBuilt: 1888,
    architecturalStyle: 'Mauresque',
    unescoHeritage: true,
    historicalPeriod: 'Ottoman',
    isFavorite: false
  },
  {
    monumentId: 'M002',
    name: 'Amphithéâtre d\'El Jem',
    city: 'El Jem',
    yearBuilt: 238,
    architecturalStyle: 'Romaine',
    unescoHeritage: true,
    historicalPeriod: 'Romaine',
    isFavorite: false
  },
  {
    monumentId: 'M003',
    name: 'Mosquée Zitouna',
    city: 'Tunis',
    yearBuilt: 732,
    architecturalStyle: 'Islamique',
    unescoHeritage: false,
    historicalPeriod: 'Médiéval',
    isFavorite: false
  }
];

@Injectable({
  providedIn: 'root'
})
export class HeritageService {
  private monuments: Monument[] = MOCK_MONUMENTS;

  constructor(private culturalHeritageService: CulturalHeritageService) {}

  getAllMonuments(): Observable<Monument[]> {
    return of(this.monuments);
  }

  getMonumentById(id: string): Observable<Monument | undefined> {
    const monument = this.monuments.find(m => m.monumentId === id);
    return of(monument);
  }

  // MODIFICATION: Ne plus utiliser de fallback mock
  getHistoricalInfo(monumentId: string): Observable<HistoricalInfo> {
    console.log(`\n=== Récupération infos historiques pour ${monumentId} ===`);
    
    return this.culturalHeritageService.getHistoricalDescription(monumentId).pipe(
      tap(response => {
        console.log('✓ Réponse SOAP reçue:', response);
      }),
      map(response => {
        // Transformer la réponse SOAP en HistoricalInfo
        const info: HistoricalInfo = {
          monumentId: response.monumentId || monumentId,
          description: response.description || '',
          historicalSignificance: response.historicalSignificance || '',
          restorationHistory: response.restorationHistory || [],
          culturalImportance: response.culturalImportance || '',
          officialClassification: response.officialClassification || ''
        };
        
        console.log('✓ Données transformées:', info);
        return info;
      }),
      catchError(error => {
        console.error('✗ ERREUR lors de la récupération:', error);
        console.error('Détails:', {
          operation: error.operation,
          status: error.status,
          message: error.message
        });
        
        // IMPORTANT: Propager l'erreur au lieu de retourner un mock
        throw error;
      })
    );
  }

  getTouristStats(region: string, year: number): Observable<TouristStats> {
    console.log(`\n=== Récupération stats touristiques ${region} ${year} ===`);
    
    return this.culturalHeritageService.getAnnualTouristStats(region, year).pipe(
      tap(response => {
        console.log('✓ Réponse SOAP reçue:', response);
      }),
      map(response => {
        const stats: TouristStats = {
          region: response.region || region,
          year: parseInt(response.year) || year,
          totalVisitors: parseInt(response.totalVisitors) || 0,
          internationalVisitors: parseInt(response.internationalVisitors) || 0,
          growthRate: parseFloat(response.growthRate) || 0,
          monthlyStats: (response.monthlyStats || []).map((stat: any) => ({
            month: stat.month || '',
            visitors: parseInt(stat.visitors) || 0
          }))
        };
        
        console.log('✓ Données transformées:', stats);
        return stats;
      }),
      catchError(error => {
        console.error('✗ ERREUR lors de la récupération:', error);
        throw error;
      })
    );
  }

  compareSites(siteAId: string, siteBId: string, criteria: string[]): Observable<any> {
    console.log(`\n=== Comparaison ${siteAId} vs ${siteBId} ===`);
    
    return this.culturalHeritageService.compareHeritageSites(siteAId, siteBId, criteria).pipe(
      tap(response => {
        console.log('✓ Réponse SOAP reçue:', response);
      }),
      map(response => {
        const comparison = {
          siteAName: response.siteAName || `Site ${siteAId}`,
          siteBName: response.siteBName || `Site ${siteBId}`,
          comparisons: response.comparisons || {},
          recommendation: response.recommendation || '',
          status: response.status || 'ERROR',
          message: response.message || ''
        };
        
        console.log('✓ Données transformées:', comparison);
        return comparison;
      }),
      catchError(error => {
        console.error('✗ ERREUR lors de la comparaison:', error);
        throw error;
      })
    );
  }

  searchMonuments(searchTerm: string, city?: string): Observable<Monument[]> {
    let results = this.monuments;
    
    if (searchTerm) {
      results = results.filter(monument =>
        monument.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        monument.city.toLowerCase().includes(searchTerm.toLowerCase()) ||
        monument.historicalPeriod.toLowerCase().includes(searchTerm.toLowerCase())
      );
    }
    
    if (city) {
      results = results.filter(monument =>
        monument.city.toLowerCase() === city.toLowerCase()
      );
    }
    
    return of(results);
  }
}