import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { ReviewService } from './review.service';
import { environment } from '../../../env/environment';
import { ReviewRequestDTO } from '../models/ride.model';

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ReviewService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should call createVehicleReview and return success', () => {
    const rideId = 123;
    const dto: ReviewRequestDTO = { rating: 5, comment: 'cool car!' };

    service.createVehicleReview(rideId, dto).subscribe();

    const req = httpMock.expectOne(`${environment.apiHost}/reviews/${rideId}/vehicle`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dto);
    req.flush({});
  });

  it('should call createDriverReview and return success', () => {
    const rideId = 123;
    const dto: ReviewRequestDTO = { rating: 2, comment: 'crazy driver' };

    service.createDriverReview(rideId, dto).subscribe();

    const req = httpMock.expectOne(`${environment.apiHost}/reviews/${rideId}/driver`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dto);
    req.flush({});
  });
});