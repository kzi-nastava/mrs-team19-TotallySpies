import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivateDriverComponent } from './activate-driver.component';

describe('ActivateDriverComponent', () => {
  let component: ActivateDriverComponent;
  let fixture: ComponentFixture<ActivateDriverComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ActivateDriverComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ActivateDriverComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
