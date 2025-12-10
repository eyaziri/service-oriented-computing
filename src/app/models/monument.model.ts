// src/app/models/monument.model.ts
export interface Monument {
  monumentId: string;
  name: string;
  city: string;
  yearBuilt: number;
  architecturalStyle: string;
  unescoHeritage: boolean;
  historicalPeriod: string;
  isFavorite?: boolean; // Propriété optionnelle
}