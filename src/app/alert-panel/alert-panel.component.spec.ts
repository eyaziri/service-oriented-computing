import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlertPanelComponent } from './alert-panel.component';

describe('AlertPanelComponent', () => {
  let component: AlertPanelComponent;
  let fixture: ComponentFixture<AlertPanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AlertPanelComponent]
    });
    fixture = TestBed.createComponent(AlertPanelComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
