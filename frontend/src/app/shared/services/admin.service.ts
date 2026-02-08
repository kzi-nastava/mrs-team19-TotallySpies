import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../../env/environment";
import { CreateDriverRequestDTO, ProfileChangeRequestDTO } from "../models/users/user.model";
import { Observable } from "rxjs";

@Injectable({ providedIn: 'root' })
export class AdminService {
  private baseUrl = `${environment.apiHost}/admin`;

  constructor(private http: HttpClient) {}

  getPendingRequests(): Observable<ProfileChangeRequestDTO[]> {
    return this.http.get<ProfileChangeRequestDTO[]>(`${this.baseUrl}/profile-change-requests`);
  }

  approveRequest(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/profile-change-requests/${id}/approve`, {});
  }

  rejectRequest(id: number): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/profile-change-requests/${id}/reject`, {});
  }

  // Metoda za kreiranje drivera sa vozilom
  createDriver(dto: CreateDriverRequestDTO): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/create-driver`, dto);
  }
}