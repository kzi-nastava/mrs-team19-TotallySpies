import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TimerCardComponent } from './timer-card.component';

describe('TimerCardComponent', () => {
  let component: TimerCardComponent;
  let fixture: ComponentFixture<TimerCardComponent>;
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TimerCardComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TimerCardComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
