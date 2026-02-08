import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable,map } from 'rxjs';
import { environment } from '../../../../env/environment';
import { RideTrackingDTO } from '../../models/ride.model';

export type RouteInfo = { distanceKm: number; estimatedTime: number };
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
    return this.http.get<any[]>(`${environment.apiHost}/vehicles/active`, {
      headers: new HttpHeaders({ 'skip': 'true' })
    }); // skip because we dont need token
  } 

  getRouteInfo(fromLat: number, fromLng: number, toLat: number, toLng: number): Observable<RouteInfo> {
    const token =
      'pk.eyJ1IjoidG90YWxseS1zcGllczMzIiwiYSI6ImNtanpxYm54dzV1MTEzZnF4M3c4ejZ0c28ifQ.iwa5IGW8kqTBZtwXvVTQcQ';

    // Mapbox expects lon,lat order
    const url =
      `https://api.mapbox.com/directions/v5/mapbox/driving/` +
      `${fromLng},${fromLat};${toLng},${toLat}` +
      `?geometries=geojson&overview=false&access_token=${token}`;

    return this.http.get<any>(url).pipe(
      map((res) => {
        const route = res?.routes?.[0];
        const distanceKm = route ? Math.round((route.distance / 1000) * 10) / 10 : 0; // 0.1km precision
        const estimatedTime = route ? Math.round(route.duration / 60) : 0; // minutes
        return { distanceKm, estimatedTime };
      })
    );
  }

  getRideLocation(rideId: number): Observable<RideTrackingDTO> {
    return this.http.get<RideTrackingDTO>(`${environment.apiHost}/rides/${rideId}/location`);
  }
}
