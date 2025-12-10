import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

// Angular Material Modules
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTabsModule } from '@angular/material/tabs';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatDialogModule } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCardModule } from '@angular/material/card';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSliderModule } from '@angular/material/slider';
import { MatChipsModule } from '@angular/material/chips';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatStepperModule } from '@angular/material/stepper';
import { MatListModule } from '@angular/material/list'; // AJOUTEZ CECI
import { MatMenuModule } from '@angular/material/menu'; // AJOUTEZ CECI POUR LE MENU
import { MatGridListModule } from '@angular/material/grid-list'; // OPTIONNEL

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomeComponent } from './components/home/home.component';
import { AttractionsSearchComponent } from './components/attractions-search/attractions-search.component';
import { AttractionDetailComponent } from './components/attraction-detail/attraction-detail.component';
import { ReservationDialogComponent } from './components/reservation-dialog/reservation-dialog.component';
import { ReviewDialogComponent } from './components/review-dialog/review-dialog.component';
import { AdminAttractionsComponent } from './admin-attractions/admin-attractions.component';
import { ConfirmDialogComponent } from './confirm-dialog/confirm-dialog.component';
import { AttractionFormDialogComponent } from './attraction-form-dialog/attraction-form-dialog.component';
import { NotificationDialogComponent } from './notification-dialog/notification-dialog.component';
import { NotificationsPanelComponent } from './notifications-panel/notifications-panel.component';
import { AlertPanelComponent } from './alert-panel/alert-panel.component';
import { TourismStatisticsComponent } from './tourism-statistics/tourism-statistics.component';
import { MonumentListComponent } from './monument-list/monument-list.component';
import { MonumentDetailsDialogComponent } from './monument-details-dialog/monument-details-dialog.component';
import { CompareMonumentsDialogComponent } from './compare-monuments-dialog/compare-monuments-dialog.component';
import { CulturalHeritageComponent } from './cultural-heritage/cultural-heritage.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    AttractionsSearchComponent,
    AttractionDetailComponent,
    ReservationDialogComponent,
    ReviewDialogComponent,
    AdminAttractionsComponent,
    ConfirmDialogComponent,
    AttractionFormDialogComponent,
    NotificationDialogComponent,
    NotificationsPanelComponent,
    AlertPanelComponent,
    TourismStatisticsComponent,
    MonumentListComponent,
    MonumentDetailsDialogComponent,
    CompareMonumentsDialogComponent,
    CulturalHeritageComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    HttpClientModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    
    // Material modules - AJOUTEZ LES NOUVEAUX MODULES
    MatListModule,       // ← AJOUTEZ CECI
    MatMenuModule,       // ← AJOUTEZ CECI POUR LE MENU DE NOTIFICATIONS
    MatGridListModule,   // ← OPTIONNEL, UTILE POUR LES GRIDS
    
    // Modules existants
    MatDatepickerModule,
    MatNativeDateModule,
    MatSliderModule,
    MatTabsModule,
    MatProgressSpinnerModule,
    MatDialogModule,
    MatTableModule,
    MatSortModule,
    MatPaginatorModule,
    MatSlideToggleModule,
    MatProgressBarModule,
    MatStepperModule,
    MatDividerModule,
    MatIconModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatSelectModule,
    MatCardModule,
    MatToolbarModule,
    MatChipsModule,
    MatBadgeModule,
    MatTooltipModule,
    MatSnackBarModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }