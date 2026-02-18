import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "../../../env/environment";
import { AdminUserDTO, CreateDriverRequestDTO, ProfileChangeRequestDTO, UserDTO } from "../models/users/user.model";
import { Observable } from "rxjs";
import { PassengerRideHistoryResponseDTO } from "../models/passenger-ride-history.model";
import { AdminRideHistoryResponseDTO } from "../models/admin-ride-history.model";
import { PanicNotificationDTO } from "../models/panic-notification.model";

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
  getAllUsers() : Observable<UserDTO[]> {
    return this.http.get<UserDTO[]>(`${this.baseUrl}/users`);
  }
 
  blockUser(id: number, reason: string): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/block/${id}`, { reason });
  }

  unblockUser(id: number): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/unblock/${id}`, {});
  }
  getNotifications() : Observable<PanicNotificationDTO[]>{
    return this.http.get<PanicNotificationDTO[]>(`${this.baseUrl}/panic-notifications`, {});
  }
  getRidesHistory(params: {
    userId: number;           
    userIndicator: number;   
    sortBy?: string;
    sortDirection?: string;
    from?: string;
    to?: string;
  }): Observable<AdminRideHistoryResponseDTO[]> {

    let httpParams = new HttpParams()
      .set('userIndicator', params.userIndicator.toString());
    if (params.sortBy) {
      httpParams = httpParams.set('sortBy', params.sortBy);
    }
    if (params.sortDirection) {
      httpParams = httpParams.set('sortDirection', params.sortDirection);
    }
    if (params.from) {
      httpParams = httpParams.set('from', params.from);
    }
    if (params.to) {
      httpParams = httpParams.set('to', params.to);
    }
    return this.http.get<AdminRideHistoryResponseDTO[]>(
      `${this.baseUrl}/${params.userId}/history`, 
      { params: httpParams }
    );
  }
}