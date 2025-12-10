import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompareMonumentsDialogComponent } from './compare-monuments-dialog.component';

describe('CompareMonumentsDialogComponent', () => {
  let component: CompareMonumentsDialogComponent;
  let fixture: ComponentFixture<CompareMonumentsDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CompareMonumentsDialogComponent]
    });
    fixture = TestBed.createComponent(CompareMonumentsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
