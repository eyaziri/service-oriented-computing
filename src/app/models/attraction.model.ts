export enum Category {
  MUSEUM = 'MUSEUM',
  MONUMENT = 'MONUMENT',
  PARK = 'PARK',
  RESTAURANT = 'RESTAURANT',
  BEACH = 'BEACH',
  SHOPPING = 'SHOPPING',
  RELIGIOUS = 'RELIGIOUS',
  HISTORICAL = 'HISTORICAL',
  ENTERTAINMENT = 'ENTERTAINMENT',
  OTHER = 'OTHER'
}

export interface Location {
  latitude: number;
  longitude: number;
  address: string;
  postalCode: string;
  city: string;
  country: string;
  fullAddress?: string;
}

export interface Attraction {
  id: number;
  name: string;
  description: string;
  category: Category;
  location: Location;
  city: string;
  entryPrice: number;
  closingHours?: string;
  openingHours?: string;
  maxCapacity?: number;
  currentVisitors: number;
  rating: number;
  totalReviews: number;
  imageUrl?: string;
  websiteUrl?: string;
  phoneNumber?: string;
  email?: string;
  averageVisitDuration?: number;
  isActive: boolean;
  isFeatured: boolean;
  createdAt: string;
  updatedAt: string;
  
  // Calculated fields
  isOpen?: boolean;
  availableSpots?: number;
  occupancyRate?: number;
}