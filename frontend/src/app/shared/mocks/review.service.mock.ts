import { of } from 'rxjs';

export class ReviewServiceMock {
  createVehicleReview(rideId: number, dto: any) {
    return of({ status: 'success' });
  }
  createDriverReview(rideId: number, dto: any) {
    return of({ status: 'success' });
  }
}