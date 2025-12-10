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