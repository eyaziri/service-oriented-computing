import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { AttractionsSearchComponent } from './components/attractions-search/attractions-search.component';
import { AttractionDetailComponent } from './components/attraction-detail/attraction-detail.component';
import { AdminAttractionsComponent } from './admin-attractions/admin-attractions.component';
import { MonumentListComponent } from './monument-list/monument-list.component';
import { CulturalHeritageComponent } from './cultural-heritage/cultural-heritage.component';
const routes: Routes = [
  { 
    path: '', 
    component: HomeComponent,
    pathMatch: 'full'
  },
{
    path: 'cultural-heritage',
    component: CulturalHeritageComponent
  },
  
  { 
    path: 'attractions', 
    component: AttractionsSearchComponent 
  },
    {
    path: 'admin/attractions',
    component: AdminAttractionsComponent,
    data: { title: 'Administration des Attractions' }
  },
  { 
    path: 'attractions/:id', 
    component: AttractionDetailComponent 
  },
  { 
    path: '**', 
    redirectTo: '' 
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }