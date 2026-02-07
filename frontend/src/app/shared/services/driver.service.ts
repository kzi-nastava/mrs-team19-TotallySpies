import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../env/environment';
import { 
  DriverActivityResponseDTO, 
  VehicleInfoResponseDTO, 
  UserProfileUpdateRequestDTO,
} from '../models/users/user.model';
import {  DriverRideHistoryDTO } from '../models/ride.model'

@Injectable({
  providedIn: 'root'
})
export class DriverService {
  private baseUrl = `${environment.apiHost}/drivers`;

  constructor(private http: HttpClient) {}

  /**
   * Dobavlja aktivnost vozača u poslednjih 24h (minute)
   * Ruta: GET /api/v1/drivers/activity
   */
  getActivity(): Observable<DriverActivityResponseDTO> {
    return this.http.get<DriverActivityResponseDTO>(`${this.baseUrl}/activity`);
  }

  /**
   * Dobavlja informacije o vozilu ulogovanog vozača
   * Ruta: GET /api/v1/drivers/vehicle-info
   */
  getVehicleInfo(): Observable<VehicleInfoResponseDTO> {
    return this.http.get<VehicleInfoResponseDTO>(`${this.baseUrl}/vehicle-info`);
  }

  getDriverHistory(from?: string, to?: string): Observable<DriverRideHistoryDTO[]> {
    let params = new HttpParams();
    if (from) params = params.set('from', from);
    if (to) params = params.set('to', to);

    return this.http.get<DriverRideHistoryDTO[]>(`${this.baseUrl}/history`, { params });
  }
}