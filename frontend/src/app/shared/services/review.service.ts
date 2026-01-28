import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { InconsistencyReportRequestDTO, ReviewRequestDTO, RideFinishResponseDTO, RideTrackingDTO } from '../models/ride.model';
import { environment } from '../../../env/environment';

@Injectable({
  providedIn: 'root',
})
export class ReviewService {
  constructor(private http: HttpClient) {}

  createVehicleReview(rideId: number, dto: ReviewRequestDTO): Observable<any> {
    return this.http.post(`${environment.apiHost}/reviews/${rideId}/vehicle`, dto);
  }

  createDriverReview(rideId: number, dto: ReviewRequestDTO): Observable<any> {
    return this.http.post(`${environment.apiHost}/reviews/${rideId}/driver`, dto);
  }
}
