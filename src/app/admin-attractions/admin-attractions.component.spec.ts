import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminAttractionsComponent } from './admin-attractions.component';

describe('AdminAttractionsComponent', () => {
  let component: AdminAttractionsComponent;
  let fixture: ComponentFixture<AdminAttractionsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AdminAttractionsComponent]
    });
    fixture = TestBed.createComponent(AdminAttractionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
