import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RideFormUnregisteredComponent } from './ride-form-unregistered.component';

describe('RideFormUnregisteredComponent', () => {
  let component: RideFormUnregisteredComponent;
  let fixture: ComponentFixture<RideFormUnregisteredComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideFormUnregisteredComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RideFormUnregisteredComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
