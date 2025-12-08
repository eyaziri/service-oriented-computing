import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttractionDetailComponent } from './attraction-detail.component';

describe('AttractionDetailComponent', () => {
  let component: AttractionDetailComponent;
  let fixture: ComponentFixture<AttractionDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AttractionDetailComponent]
    });
    fixture = TestBed.createComponent(AttractionDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
