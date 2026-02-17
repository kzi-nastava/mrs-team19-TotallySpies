import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideTrackerDriverComponent } from './ride-tracker-driver.component';

describe('RideTrackerDriverComponent', () => {
  let component: RideTrackerDriverComponent;
  let fixture: ComponentFixture<RideTrackerDriverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideTrackerDriverComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideTrackerDriverComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
