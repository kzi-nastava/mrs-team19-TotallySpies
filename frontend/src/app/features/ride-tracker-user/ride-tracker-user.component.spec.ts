import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideTrackerUserComponent } from './ride-tracker-user.component';

describe('RideTrackerUserComponent', () => {
  let component: RideTrackerUserComponent;
  let fixture: ComponentFixture<RideTrackerUserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideTrackerUserComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideTrackerUserComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
