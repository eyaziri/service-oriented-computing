// src/app/components/compare-monuments-dialog/compare-monuments-dialog.component.ts
import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Monument  } from '../models/restoration-history.model';
export interface CompareMonumentsDialogData {
  monumentA: Monument;
  monumentB: Monument;
}

@Component({
  selector: 'app-compare-monuments-dialog',
  templateUrl: './compare-monuments-dialog.component.html',
  styleUrls: ['./compare-monuments-dialog.component.scss']
})
export class CompareMonumentsDialogComponent implements OnInit {
  comparisonData: any = null;
  loading = false;
  error = '';
  
  // Données d'exemple pour la comparaison
  comparisonMetrics = [
    { label: 'Architecture', key: 'architecturalStyle' },
    { label: 'Période historique', key: 'historicalPeriod' },
    { label: 'Statut UNESCO', key: 'unescoHeritage' },
    { label: 'Année de construction', key: 'yearBuilt' },
    { label: 'Ville', key: 'city' }
  ];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: CompareMonumentsDialogData,
    private dialogRef: MatDialogRef<CompareMonumentsDialogComponent>
  ) {}

  ngOnInit() {
  }

  getComparisonKeys(): string[] {
    if (!this.comparisonData?.comparisons) return [];
    return Object.keys(this.comparisonData.comparisons);
  }

  getMonumentValue(monument: Monument, key: string): any {
    switch (key) {
      case 'architecturalStyle':
        return monument.architecturalStyle;
      case 'historicalPeriod':
        return monument.historicalPeriod;
      case 'unescoHeritage':
        return monument.unescoHeritage ? 'Oui ✓' : 'Non';
      case 'yearBuilt':
        return monument.yearBuilt;
      case 'city':
        return monument.city;
      default:
        return '-';
    }
  }

  close(): void {
    this.dialogRef.close();
  }
}