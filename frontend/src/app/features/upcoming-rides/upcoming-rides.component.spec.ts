import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpcomingRidesComponent } from './upcoming-rides.component';

describe('UpcomingRidesComponent', () => {
  let component: UpcomingRidesComponent;
  let fixture: ComponentFixture<UpcomingRidesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpcomingRidesComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UpcomingRidesComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
