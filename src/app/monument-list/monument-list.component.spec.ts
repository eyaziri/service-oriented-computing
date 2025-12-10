import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MonumentListComponent } from './monument-list.component';

describe('MonumentListComponent', () => {
  let component: MonumentListComponent;
  let fixture: ComponentFixture<MonumentListComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MonumentListComponent]
    });
    fixture = TestBed.createComponent(MonumentListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
