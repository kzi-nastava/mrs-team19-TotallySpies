import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DisplayInfoComponent } from './display-info.component';

describe('DisplayInfoComponent', () => {
  let component: DisplayInfoComponent;
  let fixture: ComponentFixture<DisplayInfoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DisplayInfoComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DisplayInfoComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
