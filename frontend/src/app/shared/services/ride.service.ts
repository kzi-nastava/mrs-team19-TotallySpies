import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CreateRideRequestDTO, CreateRideResponseDTO, RideFinishResponseDTO, RideTrackingDTO } from '../models/ride.model';
import { environment } from '../../../env/environment';
import { CancelRideDTO } from '../models/cancel-ride.model';
import { PanicRideDTO } from '../models/panic-ride.model';
import { StopRideDTO } from '../models/stop-ride.model';

@Injectable({
  providedIn: 'root',
})
export class RideService {
  constructor(private http: HttpClient) { }

  getRidesForDriver(): Observable<RideFinishResponseDTO[]> {
    return this.http.get<RideFinishResponseDTO[]>(`${environment.apiHost}/rides/scheduled`);
  }

  finishRide(id: number): Observable<RideFinishResponseDTO> {
    return this.http.put<RideFinishResponseDTO>(`${environment.apiHost}/rides/${id}/end`, {});
  }

  checkRideAccess(rideId: number): Observable<boolean> {
    return this.http.get<boolean>(`${environment.apiHost}/rides/${rideId}/access`);
  }

  getRideData(rideId: number): Observable<RideTrackingDTO> {
    return this.http.get<RideTrackingDTO>(`${environment.apiHost}/rides/${rideId}`);
  }

  reportInconsistency(rideId: number, request: any): Observable<any> {
    return this.http.post(`${environment.apiHost}/rides/${rideId}/inconsistency-report`, request, { responseType: 'text' });
  }

  cancelRide(dto : CancelRideDTO){
    return this.http.put<string>(`${environment.apiHost}/rides/cancel-ride`, dto);
  }

  createRide(dto: CreateRideRequestDTO): Observable<CreateRideResponseDTO> {
    return this.http.post<CreateRideResponseDTO>(`${environment.apiHost}/rides/create`, dto, {
      headers: { 'Content-Type': 'application/json' }
    });
  }

  startRide(rideId: number) {
    return this.http.put(`${environment.apiHost}/rides/${rideId}/start`,null,
      { responseType: 'text' }
    );
  }

  panicRide(dto : PanicRideDTO){
    return this.http.put<string>(`${environment.apiHost}/rides/panic`, dto);
  }
  stopRide(dto: StopRideDTO) {
  return this.http.put(
    `${environment.apiHost}/rides/stop-ride`,
    dto,
    { responseType: 'text' } 
  );
}
}
