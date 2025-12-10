// src/app/services/cultural-heritage.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError, tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CulturalHeritageService {
  private apiGatewayUrl = 'http://localhost:8080';
  private soapEndpoint = `${this.apiGatewayUrl}/soap/ws`;
  
  private readonly headers = new HttpHeaders({
    'Content-Type': 'text/xml',
    });

  constructor(private http: HttpClient) {}

  private callSoapService(soapRequest: string, operation: string): Observable<any> {
    console.log(`=== Appel SOAP: ${operation} ===`);
    console.log(`URL: ${this.soapEndpoint}`);
    console.log(`Requête SOAP:\n`, soapRequest);
    
    return this.http.post(this.soapEndpoint, soapRequest, {
      headers: this.headers,
      responseType: 'text',
      observe: 'response'
    }).pipe(
      tap(response => {
        console.log(`Status: ${response.status}`);
        console.log(`Response Headers:`, response.headers.keys());
      }),
      map(response => {
        console.log('=== Réponse SOAP brute ===\n', response.body);
        
        if (!response.body) {
          throw new Error('Réponse vide du serveur SOAP');
        }
        
        return this.parseSoapResponse(response.body, operation);
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('=== ERREUR SOAP ===');
        console.error('Status:', error.status);
        console.error('Message:', error.message);
        console.error('Error:', error.error);
        
        // NE PAS retourner de mock ici, propager l'erreur
        return throwError(() => ({
          operation,
          status: error.status,
          message: error.message,
          details: error.error
        }));
      })
    );
  }

  private parseSoapResponse(xmlResponse: string, operation: string): any {
    console.log(`=== Parsing réponse pour ${operation} ===`);
    
    const parser = new DOMParser();
    const xmlDoc = parser.parseFromString(xmlResponse, 'text/xml');
    
    // Vérifier les erreurs de parsing
    const parserError = xmlDoc.getElementsByTagName('parsererror');
    if (parserError.length > 0) {
      console.error('Erreur de parsing XML:', parserError[0].textContent);
      throw new Error(`Erreur de parsing XML: ${parserError[0].textContent}`);
    }
    
    // Vérifier les fault SOAP
    const soapFault = xmlDoc.getElementsByTagName('soap:Fault')[0] || 
                      xmlDoc.getElementsByTagName('SOAP-ENV:Fault')[0];
    if (soapFault) {
      const faultString = soapFault.getElementsByTagName('faultstring')[0]?.textContent;
      throw new Error(`SOAP Fault: ${faultString || 'Erreur inconnue'}`);
    }
    
    // Chercher l'élément de réponse avec différents préfixes possibles
    const possiblePrefixes = ['', 'ns2:', 'tns:', 'soap:'];
    let responseElement = null;
    
    for (const prefix of possiblePrefixes) {
      responseElement = xmlDoc.getElementsByTagName(`${prefix}${operation}Response`)[0];
      if (responseElement) {
        console.log(`Element trouvé avec préfixe: ${prefix}`);
        break;
      }
    }
    
    if (!responseElement) {
      console.error('Élément de réponse non trouvé');
      console.log('Structure XML:', new XMLSerializer().serializeToString(xmlDoc));
      throw new Error(`Élément ${operation}Response non trouvé dans la réponse`);
    }
    
    // Fonction helper pour extraire des valeurs
    const extractValue = (element: Element, tagName: string): string | null => {
      // Essayer sans préfixe
      let found = element.getElementsByTagName(tagName)[0];
      if (found) return found.textContent;
      
      // Essayer avec différents préfixes
      for (const prefix of possiblePrefixes) {
        found = element.getElementsByTagName(`${prefix}${tagName}`)[0];
        if (found) return found.textContent;
      }
      
      return null;
    };
    
    // Parser selon l'opération
    switch(operation) {
      case 'GetHistoricalDescription':
        return this.parseHistoricalDescription(responseElement, extractValue);
        
      case 'GetAnnualTouristStats':
        return this.parseTouristStats(responseElement, extractValue);
        
      case 'CompareHeritageSites':
        return this.parseComparison(responseElement, extractValue);
        
      default:
        throw new Error(`Opération non supportée: ${operation}`);
    }
  }

  private parseHistoricalDescription(element: Element, extractValue: Function): any {
    const status = extractValue(element, 'status');
    const message = extractValue(element, 'message');
    
    if (status === 'ERROR') {
      throw new Error(message || 'Erreur lors de la récupération des informations historiques');
    }
    
    // Extraire historicalInfo
    const infoElement = element.getElementsByTagName('historicalInfo')[0];
    if (!infoElement) {
      throw new Error('Element historicalInfo non trouvé');
    }
    
    const restorationHistory: string[] = [];
    const restoElements = infoElement.getElementsByTagName('restorationHistory');
    for (let i = 0; i < restoElements.length; i++) {
      const text = restoElements[i].textContent;
      if (text) restorationHistory.push(text);
    }
    
    return {
      monumentId: extractValue(infoElement, 'monumentId'),
      description: extractValue(infoElement, 'description'),
      historicalSignificance: extractValue(infoElement, 'historicalSignificance'),
      restorationHistory,
      culturalImportance: extractValue(infoElement, 'culturalImportance'),
      officialClassification: extractValue(infoElement, 'officialClassification'),
      status,
      message
    };
  }

  private parseTouristStats(element: Element, extractValue: Function): any {
    const status = extractValue(element, 'status');
    const message = extractValue(element, 'message');
    
    if (status === 'ERROR') {
      throw new Error(message || 'Erreur lors de la récupération des statistiques');
    }
    
    const statsElement = element.getElementsByTagName('touristStats')[0];
    if (!statsElement) {
      throw new Error('Element touristStats non trouvé');
    }
    
    const monthlyStats: any[] = [];
    const monthlyStatsElement = statsElement.getElementsByTagName('monthlyStats')[0];
    
    if (monthlyStatsElement) {
      const statEntries = monthlyStatsElement.getElementsByTagName('monthlyStat');
      for (let i = 0; i < statEntries.length; i++) {
        const month = extractValue(statEntries[i], 'month');
        const visitors = extractValue(statEntries[i], 'visitors');
        
        if (month && visitors) {
          monthlyStats.push({
            month,
            visitors: parseInt(visitors)
          });
        }
      }
    }
    
    return {
      region: extractValue(statsElement, 'region'),
      year: parseInt(extractValue(statsElement, 'year') || '0'),
      totalVisitors: parseInt(extractValue(statsElement, 'totalVisitors') || '0'),
      internationalVisitors: parseInt(extractValue(statsElement, 'internationalVisitors') || '0'),
      growthRate: parseFloat(extractValue(statsElement, 'growthRate') || '0'),
      monthlyStats,
      status,
      message
    };
  }

  private parseComparison(element: Element, extractValue: Function): any {
    const status = extractValue(element, 'status');
    const message = extractValue(element, 'message');
    
    if (status === 'ERROR') {
      throw new Error(message || 'Erreur lors de la comparaison');
    }
    
    const comparisons: any = {};
    const comparisonsElement = element.getElementsByTagName('comparisons')[0];
    
    if (comparisonsElement) {
      const entries = comparisonsElement.getElementsByTagName('entry');
      for (let i = 0; i < entries.length; i++) {
        const key = extractValue(entries[i], 'key');
        const value = extractValue(entries[i], 'value');
        
        if (key && value) {
          comparisons[key] = value;
        }
      }
    }
    
    return {
      siteAName: extractValue(element, 'siteAName'),
      siteBName: extractValue(element, 'siteBName'),
      comparisons,
      recommendation: extractValue(element, 'recommendation'),
      status,
      message
    };
  }

  // APIs publiques
  getHistoricalDescription(monumentId: string): Observable<any> {
    const soapRequest = this.buildSoapRequest(
      'GetHistoricalDescriptionRequest',
      `<smart:monumentId>${this.escapeXml(monumentId)}</smart:monumentId>`
    );
    
    return this.callSoapService(soapRequest, 'GetHistoricalDescription');
  }

  getAnnualTouristStats(region: string, year: number): Observable<any> {
    const soapRequest = this.buildSoapRequest(
      'GetAnnualTouristStatsRequest',
      `<smart:region>${this.escapeXml(region)}</smart:region>
       <smart:year>${year}</smart:year>`
    );
    
    return this.callSoapService(soapRequest, 'GetAnnualTouristStats');
  }

  compareHeritageSites(siteAId: string, siteBId: string, criteria: string[]): Observable<any> {
    const criteriaXml = criteria
      .map(c => `<smart:comparisonCriteria>${this.escapeXml(c)}</smart:comparisonCriteria>`)
      .join('\n       ');
    
    const soapRequest = this.buildSoapRequest(
      'CompareHeritageSitesRequest',
      `<smart:siteAId>${this.escapeXml(siteAId)}</smart:siteAId>
       <smart:siteBId>${this.escapeXml(siteBId)}</smart:siteBId>
       ${criteriaXml}`
    );
    
    return this.callSoapService(soapRequest, 'CompareHeritageSites');
  }

  private buildSoapRequest(operation: string, bodyContent: string): string {
    return `<?xml version="1.0" encoding="UTF-8"?>
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
                 xmlns:smart="http://smarttourism.com/soap">
  <soapenv:Header/>
  <soapenv:Body>
    <smart:${operation}>
      ${bodyContent}
    </smart:${operation}>
  </soapenv:Body>
</soapenv:Envelope>`;
  }

  private escapeXml(unsafe: string): string {
    return unsafe
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;')
      .replace(/"/g, '&quot;')
      .replace(/'/g, '&apos;');
  }

  // Test de connexion
  testConnection(): Observable<any> {
    console.log('=== Test de connexion SOAP ===');
    return this.getHistoricalDescription('M001');
  }
}