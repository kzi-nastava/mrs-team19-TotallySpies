import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FavouriteRideDTO } from '../models/ride.model';
import { environment } from '../../../env/environment';

@Injectable({
  providedIn: 'root'
})
export class FavouriteService {

  constructor(private http: HttpClient) {}

  getFavourites(): Observable<FavouriteRideDTO[]> {
    return this.http.get<FavouriteRideDTO[]>(`${environment.apiHost}/favourites`);
  }
}