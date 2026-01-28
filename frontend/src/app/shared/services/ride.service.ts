import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { InconsistencyReportRequestDTO, RideFinishResponseDTO, RideTrackingDTO } from '../models/ride.model';
import { environment } from '../../../env/environment';

@Injectable({
  providedIn: 'root',
})
export class RideService {
  constructor(private http: HttpClient) {}

  getRidesForDriver(): Observable<RideFinishResponseDTO[]> {
    return this.http.get<RideFinishResponseDTO[]>(`${environment.apiHost}/rides/scheduled`);
  }

  finishRide(id: number): Observable<RideFinishResponseDTO> {
    return this.http.put<RideFinishResponseDTO>(`${environment.apiHost}/rides/${id}/end`, {});
  }

  getRideLocation(rideId: number): Observable<RideTrackingDTO> {
    return this.http.get<RideTrackingDTO>(`${environment.apiHost}/rides/${rideId}/location`);
  }

  reportInconsistency(rideId: number, request: any): Observable<any> {
    return this.http.post(`${environment.apiHost}/rides/${rideId}/inconsistency-report`, request, { responseType: 'text' });
  }
}
