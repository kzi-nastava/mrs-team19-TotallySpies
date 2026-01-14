import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideOrderingComponent } from './ride-ordering.component';

describe('RideOrderingComponent', () => {
  let component: RideOrderingComponent;
  let fixture: ComponentFixture<RideOrderingComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideOrderingComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideOrderingComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
