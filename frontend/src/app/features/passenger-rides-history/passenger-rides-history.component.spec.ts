import { PassengerRidesHistoryComponent } from './passenger-rides-history.component';

describe('PassengerRidesHistoryComponent Logic', () => {
  let component: any;

  beforeEach(() => {
    component = new PassengerRidesHistoryComponent(null!, null!, null!, null!, null!);
  });

  it('should return true if ride finished less than 3 days ago', () => {
    const today = new Date();
    const result = component.canRateRide(today.toISOString());
    expect(result).toBeTrue();
  });

  it('should return false if ride finished more than 3 days ago', () => {
    const fourDaysAgo = new Date();
    fourDaysAgo.setDate(fourDaysAgo.getDate() - 4);
    
    const result = component.canRateRide(fourDaysAgo.toISOString());
    expect(result).toBeFalse();
  });

  it('should return true if ride finished exactly 72 hours ago (boundary case)', () => {
    const threeDaysInMillis = 3 * 24 * 60 * 60 * 1000;
    const now = new Date().getTime();
    
    const exactlyThreeDaysAgo = new Date(now - threeDaysInMillis);
    
    const result = component.canRateRide(exactlyThreeDaysAgo.toISOString());
    
    // in code <=
    expect(result).toBeTrue();
  });

  it('should return false if finishedAt is null', () => {
    expect(component.canRateRide(null)).toBeFalse();
  });
});