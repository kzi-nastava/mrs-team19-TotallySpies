import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { NotificationDTO } from '../models/notification.model';
import { environment } from '../../../env/environment';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private baseUrl = `${environment.apiHost}/notifications`;
  
  constructor(private http: HttpClient) { }

  getNotifications(): Observable<NotificationDTO[]> {
    return this.http.get<NotificationDTO[]>(`${this.baseUrl}`);
  }

  markAsRead(id: number): Observable<any> {
    return this.http.put(`${environment.apiHost}/notifications/${id}/read`, {});
  }
}