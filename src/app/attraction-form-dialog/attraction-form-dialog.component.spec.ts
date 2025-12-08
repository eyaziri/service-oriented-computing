import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttractionFormDialogComponent } from './attraction-form-dialog.component';

describe('AttractionFormDialogComponent', () => {
  let component: AttractionFormDialogComponent;
  let fixture: ComponentFixture<AttractionFormDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AttractionFormDialogComponent]
    });
    fixture = TestBed.createComponent(AttractionFormDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
