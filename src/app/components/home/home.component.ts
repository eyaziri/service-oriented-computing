import { Component, ElementRef, ViewChild } from '@angular/core';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  @ViewChild('searchSection') searchSection!: ElementRef;

  scrollToSearch(): void {
    this.searchSection.nativeElement.scrollIntoView({ 
      behavior: 'smooth', 
      block: 'start' 
    });
  }
}