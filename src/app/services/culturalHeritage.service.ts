import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

export interface Monument {
  monumentId: string;
  name: string;
  city: string;
  yearBuilt: number;
  architecturalStyle: string;
  unescoHeritage: boolean;
  historicalPeriod: string;
}

export interface HistoricalInfo {
  monumentId: string;
  description: string;
  historicalSignificance: string;
  restorationHistory: string[];
  culturalImportance: string;
  officialClassification: string;
}

export interface TouristStats {
  region: string;
  year: number;
  totalVisitors: number;
  internationalVisitors: number;
  monthlyStats: { month: string, visitors: number }[];
  growthRate: number;
}

export interface ComparisonResult {
  siteAName: string;
  siteBName: string;
  comparisons: { [key: string]: string };
  recommendation: string;
  status: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class CulturalHeritageService {
  private apiGatewayUrl = 'http://localhost:8080/soap/ws';
  private headers = new HttpHeaders({
    'Content-Type': 'text/xml',
    'Accept': 'text/xml'
  });

  constructor(private http: HttpClient) {}

  // Méthode utilitaire utilisant querySelector
  private getXmlText(doc: Document, selector: string): string {
    const element = doc.querySelector(selector);
    return element?.textContent?.trim() || '';
  }

  // Méthode pour obtenir tous les éléments correspondant à un sélecteur
  private getAllXmlElements(doc: Document, selector: string): Element[] {
    const nodeList = doc.querySelectorAll(selector);
    const elements: Element[] = [];
    nodeList.forEach((node: Element) => {
      elements.push(node);
    });
    return elements;
  }

  // 1. Récupérer les informations historiques d'un monument
  getHistoricalDescription(monumentId: string): Observable<HistoricalInfo> {
    const soapRequest = `
      <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                        xmlns:soap="http://smarttourism.com/soap">
        <soapenv:Header/>
        <soapenv:Body>
          <soap:GetHistoricalDescriptionRequest>
            <soap:monumentId>${monumentId}</soap:monumentId>
          </soap:GetHistoricalDescriptionRequest>
        </soapenv:Body>
      </soapenv:Envelope>
    `;

    return this.http.post(this.apiGatewayUrl, soapRequest, {
      headers: this.headers,
      responseType: 'text'
    }).pipe(
      map(response => this.extractHistoricalInfoSimple(response)),
      catchError(this.handleError<HistoricalInfo>('getHistoricalDescription'))
    );
  }

  private extractHistoricalInfoSimple(xmlResponse: string): HistoricalInfo {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlResponse, 'text/xml');
    
    // Utiliser querySelector pour une extraction plus simple
    const monumentId = this.getXmlText(xmlDoc, 'monumentId, ns2\\:monumentId');
    const description = this.getXmlText(xmlDoc, 'description, ns2\\:description');
    const historicalSignificance = this.getXmlText(xmlDoc, 'historicalSignificance, ns2\\:historicalSignificance');
    const culturalImportance = this.getXmlText(xmlDoc, 'culturalImportance, ns2\\:culturalImportance');
    const officialClassification = this.getXmlText(xmlDoc, 'officialClassification, ns2\\:officialClassification');
    
    // Extraire restorationHistory
    const restorationElements = this.getAllXmlElements(xmlDoc, 'restorationHistory, ns2\\:restorationHistory');
    const restorationHistory = restorationElements.map(el => el.textContent?.trim() || '');
    
    return {
      monumentId: monumentId || 'Inconnu',
      description: description || 'Aucune description disponible',
      historicalSignificance: historicalSignificance || 'Non documenté',
      restorationHistory: restorationHistory.filter(h => h !== ''),
      culturalImportance: culturalImportance || 'Importance culturelle non spécifiée',
      officialClassification: officialClassification || 'Non classé'
    };
  }

  // 2. Récupérer les statistiques touristiques
  getAnnualTouristStats(region: string, year?: number): Observable<TouristStats> {
    const yearParam = year ? `<soap:year>${year}</soap:year>` : '';
    
    const soapRequest = `
      <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                        xmlns:soap="http://smarttourism.com/soap">
        <soapenv:Header/>
        <soapenv:Body>
          <soap:GetAnnualTouristStatsRequest>
            <soap:region>${region}</soap:region>
            ${yearParam}
          </soap:GetAnnualTouristStatsRequest>
        </soapenv:Body>
      </soapenv:Envelope>
    `;

    return this.http.post(this.apiGatewayUrl, soapRequest, {
      headers: this.headers,
      responseType: 'text'
    }).pipe(
      map(response => this.extractTouristStatsSimple(response)),
      catchError(this.handleError<TouristStats>('getAnnualTouristStats'))
    );
  }

  private extractTouristStatsSimple(xmlResponse: string): TouristStats {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlResponse, 'text/xml');
    
    // Extraire les données de base
    const region = this.getXmlText(xmlDoc, 'region, ns2\\:region');
    const yearText = this.getXmlText(xmlDoc, 'year, ns2\\:year');
    const totalVisitorsText = this.getXmlText(xmlDoc, 'totalVisitors, ns2\\:totalVisitors');
    const internationalVisitorsText = this.getXmlText(xmlDoc, 'internationalVisitors, ns2\\:internationalVisitors');
    const growthRateText = this.getXmlText(xmlDoc, 'growthRate, ns2\\:growthRate');
    
    // Extraire les statistiques mensuelles
    const monthlyElements = this.getAllXmlElements(xmlDoc, 'monthlyStat, ns2\\:monthlyStat');
    const monthlyStats = monthlyElements.map(el => ({
      month: this.getXmlTextFromElement(el, 'month, ns2\\:month'),
      visitors: parseInt(this.getXmlTextFromElement(el, 'visitors, ns2\\:visitors')) || 0
    }));
    
    return {
      region: region || 'Région inconnue',
      year: parseInt(yearText) || 2023,
      totalVisitors: parseInt(totalVisitorsText) || 0,
      internationalVisitors: parseInt(internationalVisitorsText) || 0,
      monthlyStats: monthlyStats,
      growthRate: parseFloat(growthRateText) || 0.0
    };
  }

  private getXmlTextFromElement(element: Element, selector: string): string {
    const child = element.querySelector(selector);
    return child?.textContent?.trim() || '';
  }

  // 3. Comparer deux sites patrimoniaux
  compareHeritageSites(siteAId: string, siteBId: string, criteria: string[]): Observable<ComparisonResult> {
    const criteriaXml = criteria.map(c => `<soap:comparisonCriteria>${c}</soap:comparisonCriteria>`).join('');
    
    const soapRequest = `
      <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                        xmlns:soap="http://smarttourism.com/soap">
        <soapenv:Header/>
        <soapenv:Body>
          <soap:CompareHeritageSitesRequest>
            <soap:siteAId>${siteAId}</soap:siteAId>
            <soap:siteBId>${siteBId}</soap:siteBId>
            ${criteriaXml}
          </soap:CompareHeritageSitesRequest>
        </soapenv:Body>
      </soapenv:Envelope>
    `;

    return this.http.post(this.apiGatewayUrl, soapRequest, {
      headers: this.headers,
      responseType: 'text'
    }).pipe(
      map(response => this.extractComparisonResultSimple(response)),
      catchError(this.handleError<ComparisonResult>('compareHeritageSites'))
    );
  }

  private extractComparisonResultSimple(xmlResponse: string): ComparisonResult {
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlResponse, 'text/xml');
    
    // Extraire les informations de base
    const siteAName = this.getXmlText(xmlDoc, 'siteAName, ns2\\:siteAName');
    const siteBName = this.getXmlText(xmlDoc, 'siteBName, ns2\\:siteBName');
    const recommendation = this.getXmlText(xmlDoc, 'recommendation, ns2\\:recommendation');
    const status = this.getXmlText(xmlDoc, 'status, ns2\\:status');
    const message = this.getXmlText(xmlDoc, 'message, ns2\\:message');
    
    // Extraire les comparaisons
    const comparisons: { [key: string]: string } = {};
    
    // Chercher tous les éléments qui ne sont pas structurels
    const allElements = this.getAllXmlElements(xmlDoc, '*');
    const structuralTags = ['envelope', 'body', 'header', 'response', 'status', 'message', 'recommendation', 'siteaname', 'sitebname'];
    
    allElements.forEach(element => {
      const tagName = element.tagName.toLowerCase().replace('ns2:', '').replace('soap:', '');
      const text = element.textContent?.trim() || '';
      
      if (text && !structuralTags.includes(tagName)) {
        comparisons[tagName] = text;
      }
    });
    
    return {
      siteAName: siteAName || 'Site A',
      siteBName: siteBName || 'Site B',
      comparisons: comparisons,
      recommendation: recommendation || 'Aucune recommandation disponible',
      status: status || 'UNKNOWN',
      message: message || 'Réponse non analysable'
    };
  }

  // Gestion des erreurs
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed:`, error);
      
      if (result !== undefined) {
        return of(result);
      }
      
      // Retourner des valeurs par défaut
      return of({} as T);
    };
  }

  // Liste de monuments fictifs pour les démos
  getSampleMonuments(): Monument[] {
    return [
      {
        monumentId: 'M001',
        name: 'Musée National du Bardo',
        city: 'Tunis',
        yearBuilt: 1888,
        architecturalStyle: 'Mauresque',
        unescoHeritage: true,
        historicalPeriod: 'Ottoman'
      },
      {
        monumentId: 'M002',
        name: 'Mosquée Zitouna',
        city: 'Tunis',
        yearBuilt: 698,
        architecturalStyle: 'Islamique',
        unescoHeritage: true,
        historicalPeriod: 'Omeyyade'
      },
      {
        monumentId: 'M003',
        name: 'Amphithéâtre de Carthage',
        city: 'Carthage',
        yearBuilt: 150,
        architecturalStyle: 'Romaine',
        unescoHeritage: true,
        historicalPeriod: 'Romaine'
      },
      {
        monumentId: 'M004',
        name: 'Médina de Tunis',
        city: 'Tunis',
        yearBuilt: 698,
        architecturalStyle: 'Arabo-Islamique',
        unescoHeritage: true,
        historicalPeriod: 'Médiévale'
      }
    ];
  }

  // Tester la connexion
  testConnection(): Observable<boolean> {
    return this.getHistoricalDescription('M001').pipe(
      map(response => {
        console.log('Test connection response:', response);
        return !!response && !!response.monumentId && response.monumentId !== 'ERROR';
      }),
      catchError(() => of(false))
    );
  }
}