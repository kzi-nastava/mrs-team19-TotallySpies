import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpcomingRideCardComponent } from './upcoming-ride-card.component';

describe('UpcomingRideCardComponent', () => {
  let component: UpcomingRideCardComponent;
  let fixture: ComponentFixture<UpcomingRideCardComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpcomingRideCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpcomingRideCardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
