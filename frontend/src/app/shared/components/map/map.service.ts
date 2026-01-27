import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../env/environment';

@Injectable({
  providedIn: 'root',
})
//todo : make interface/class in shared folder to handle specific data from API, DONT use any data type
export class MapService {
  constructor(private http: HttpClient) {}

  search(street: string): Observable<any> {
    //for given street, returns list of coordinates that match that street
    return this.http.get('https://nominatim.openstreetmap.org/search?format=json&q=' + street);
  }

  reverseSearch(lat: number, lon: number): Observable<any> {
    //for given coordinates, returns list of streets
    return this.http.get(
      `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&<params>`
    );
  }

  getAllVehiclePositions(): Observable<any[]> {
 
  return this.http.get<any[]>(`${environment.apiHost}/vehicles/active`);
}
}
