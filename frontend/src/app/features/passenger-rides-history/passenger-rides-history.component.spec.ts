import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PassengerRidesHistoryComponent } from './passenger-rides-history.component';

describe('PassengerRidesHistoryComponent', () => {
  let component: PassengerRidesHistoryComponent;
  let fixture: ComponentFixture<PassengerRidesHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PassengerRidesHistoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PassengerRidesHistoryComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
