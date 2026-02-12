import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../../env/environment";
import { AdminUserDTO, CreateDriverRequestDTO, ProfileChangeRequestDTO } from "../models/users/user.model";
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

   getDriversList(): Observable<AdminUserDTO[]> {
    return this.http.get<AdminUserDTO[]>(`${this.baseUrl}/drivers`);
  }

  getUsersList(): Observable<AdminUserDTO[]> {
    return this.http.get<AdminUserDTO[]>(`${this.baseUrl}/passengers`);
  }

  blockUser(id: number, reason: string): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/block/${id}`, { reason });
  }

  unblockUser(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/unblock/${id}`, {});
  }
}