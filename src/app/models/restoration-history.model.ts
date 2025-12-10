// src/app/models/monument.model.ts
export interface Monument {
  monumentId: string;
  name: string;
  city: string;
  yearBuilt: number;
  architecturalStyle: string;
  unescoHeritage: boolean;
  historicalPeriod: string;
}

// src/app/models/historical-info.model.ts
export interface HistoricalInfo {
  monumentId: string;
  description: string;
  historicalSignificance: string;
  restorationHistory: string[];
  culturalImportance: string;
  officialClassification: string;
}

// src/app/models/tourist-stats.model.ts
export interface TouristStats {
  region: string;
  year: number;
  totalVisitors: number;
  internationalVisitors: number;
  growthRate: number;
  monthlyStats: MonthlyStat[];
}

export interface MonthlyStat {
  month: string;
  visitors: number;
}

// src/app/models/restoration-history.model.ts
export interface RestorationHistory {
  id: number;
  seq: number;
  note: string;
  restorationDate: Date;
  monument: Monument;
}