import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CulturalHeritageComponent } from './cultural-heritage.component';

describe('CulturalHeritageComponent', () => {
  let component: CulturalHeritageComponent;
  let fixture: ComponentFixture<CulturalHeritageComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CulturalHeritageComponent]
    });
    fixture = TestBed.createComponent(CulturalHeritageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
