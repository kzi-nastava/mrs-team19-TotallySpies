import { Component, AfterViewInit } from '@angular/core';
import * as L from 'leaflet';
import { MapService } from './map.service';

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
})
export class MapComponent implements AfterViewInit {
  private map: any;

  constructor(private mapService: MapService) {}

  setRoute(): void {
    //defining a route
    const routeControl = L.Routing.control({
      waypoints: [L.latLng(57.74, 11.94), L.latLng(57.6792, 11.949)],
      router: L.routing.mapbox(
        'pk.eyJ1IjoidG90YWxseS1zcGllczMzIiwiYSI6ImNtanpxYm54dzV1MTEzZnF4M3c4ejZ0c28ifQ.iwa5IGW8kqTBZtwXvVTQcQ',
        { profile: 'mapbox/walking' }
      ),
    }).addTo(this.map);

    routeControl.on('routesfound', function (e) {
      var routes = e.routes;
      var summary = routes[0].summary;
      alert(
        'Total distance is ' +
          summary.totalDistance / 1000 +
          ' km and total time is ' +
          Math.round((summary.totalTime % 3600) / 60) +
          ' minutes'
      );
    });
  }

  private initMap(): void {
    this.map = L.map('map', {
      center: [45.2396, 19.8227],
      zoom: 13,
    });

    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 18,
      minZoom: 3,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>',
    });

    tiles.addTo(this.map);
    this.setRoute();
  }
  search(): void {
    this.mapService.search('Vojvodjanska 36, Novi Sad').subscribe({
      next: (result) => {
        console.log(result);
        L.marker([result[0].lat, result[0].lon])
          .addTo(this.map)
          .bindPopup('Pozdrav iz Vojvodjanske 36.')
          .openPopup();
      },
      error: () => {},
    });
  }

  registerOnClick(): void {
    this.map.on('click', (e: any) => {
      const coord = e.latlng;
      const lat = coord.lat;
      const lng = coord.lng;
      this.mapService.reverseSearch(lat, lng).subscribe((res) => {
        console.log(res.display_name);
      });
      console.log('You clicked the map at latitude: ' + lat + ' and longitude: ' + lng);
      const mp = new L.Marker([lat, lng]).addTo(this.map);
      alert(mp.getLatLng());
    });
  }

  ngAfterViewInit(): void {
    let DefaultIcon = L.icon({
      iconUrl: 'icons/map-icons/map-marker-icon.png',
      iconSize: [25, 25],
    });

    L.Marker.prototype.options.icon = DefaultIcon;
    this.initMap();
    this.registerOnClick();
  }
}
