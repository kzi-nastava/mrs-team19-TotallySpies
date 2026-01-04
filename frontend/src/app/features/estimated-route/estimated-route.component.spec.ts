import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EstimatedRouteComponent } from './estimated-route.component';

describe('EstimatedRouteComponent', () => {
  let component: EstimatedRouteComponent;
  let fixture: ComponentFixture<EstimatedRouteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EstimatedRouteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EstimatedRouteComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
