import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../env/environment';
import { 
  DriverActivityResponseDTO, 
  VehicleInfoResponseDTO, 
  UserProfileUpdateRequestDTO 
} from '../models/users/user.model';

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
}