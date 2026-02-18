import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { ChangePasswordRequestDTO, UserProfileResponseDTO, UserProfileUpdateRequestDTO } from "../models/users/user.model";
import { Observable } from "rxjs";
import { environment } from "../../../env/environment";
import { ReportResponseDTO } from "../models/report.model";
import { PassengerRideHistoryResponseDTO } from "../models/passenger-ride-history.model";

@Injectable({ providedIn: 'root' })
export class UserService {

  private baseUrl = `${environment.apiHost}/users`;

  constructor(private http: HttpClient) {}

  getProfile(): Observable<UserProfileResponseDTO> {
    return this.http.get<UserProfileResponseDTO>(`${this.baseUrl}/profile`);
  }

  updateProfile(request: UserProfileUpdateRequestDTO): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/profile`, request);
  }

  changePassword(request: ChangePasswordRequestDTO): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/change-password`, request);
  }

  uploadProfileImage(file: File): Observable<void> {
    const formData = new FormData();
    formData.append('image', file);
    return this.http.put<void>(`${this.baseUrl}/profile/image`, formData);
  }

  getReport(from: string, to: string, targetEmail?: string): Observable<ReportResponseDTO> {
  let params = new HttpParams()
    .set('from', from + 'T00:00:00')
    .set('to', to + 'T23:59:59');
  
    if (targetEmail) {
      params = params.set('targetEmail', targetEmail);
    }

    return this.http.get<ReportResponseDTO>(`${this.baseUrl}/report`, { params });
  }
  getRidesHistory(params?: {
    sortBy?: string;
    sortDirection?: string;
    from?: string; // ISO datetime
    to?: string;   // ISO datetime
  }) : Observable<PassengerRideHistoryResponseDTO[]>{
    let httpParams = new HttpParams();

    if (params?.sortBy) httpParams = httpParams.set('sortBy', params.sortBy);
    if (params?.sortDirection) httpParams = httpParams.set('sortDirection', params.sortDirection);
    if (params?.from) httpParams = httpParams.set('from', params.from);
    if (params?.to) httpParams = httpParams.set('to', params.to);

    return this.http.get<PassengerRideHistoryResponseDTO[]>(`${this.baseUrl}/history`, {params : httpParams});
  }
}