import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminRideTrackingComponent } from './admin-ride-tracking.component';

describe('AdminRideTrackingComponent', () => {
  let component: AdminRideTrackingComponent;
  let fixture: ComponentFixture<AdminRideTrackingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AdminRideTrackingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminRideTrackingComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
