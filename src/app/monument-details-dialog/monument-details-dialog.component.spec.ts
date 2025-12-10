import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonumentDetailsDialogComponent } from './monument-details-dialog.component';

describe('MonumentDetailsDialogComponent', () => {
  let component: MonumentDetailsDialogComponent;
  let fixture: ComponentFixture<MonumentDetailsDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MonumentDetailsDialogComponent]
    });
    fixture = TestBed.createComponent(MonumentDetailsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
