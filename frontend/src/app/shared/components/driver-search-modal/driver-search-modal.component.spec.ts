import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverSearchModalComponent } from './driver-search-modal.component';

describe('DriverSearchModalComponent', () => {
  let component: DriverSearchModalComponent;
  let fixture: ComponentFixture<DriverSearchModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DriverSearchModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DriverSearchModalComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
