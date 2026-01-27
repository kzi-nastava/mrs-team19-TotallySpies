import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RideFinishResponseDTO } from '../models/ride.model';
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
}
