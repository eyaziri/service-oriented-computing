import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AttractionsSearchComponent } from './attractions-search.component';

describe('AttractionsSearchComponent', () => {
  let component: AttractionsSearchComponent;
  let fixture: ComponentFixture<AttractionsSearchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AttractionsSearchComponent]
    });
    fixture = TestBed.createComponent(AttractionsSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
