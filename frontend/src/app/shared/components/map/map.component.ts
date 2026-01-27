import {
  Component,
  AfterViewInit,
  Input,
  Output,
  EventEmitter,
  SimpleChanges,
  OnChanges,
  OnDestroy,
} from '@angular/core';
import * as L from 'leaflet';
import { MapService } from './map.service';
import 'leaflet-routing-machine';
import { interval, Subscription, startWith, switchMap } from 'rxjs'; //for polling
export type RouteInfo = { distanceKm: number; estimatedTime: number };

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
  standalone: true,
})
export class MapComponent implements AfterViewInit, OnDestroy {
  private map!: L.Map;
  private routeControl?: L.Routing.Control;
  private vehicleMarkers: Map<number, L.Marker> = new Map();
  private pollingSubscription?: Subscription;
  @Input() pickupAddress = '';
  @Input() destinationAddress = '';
  @Output() routeInfo = new EventEmitter<RouteInfo>();

  private greenIcon = L.icon({
    iconUrl: 'icons/car-icon-green.png',
    iconSize: [32, 32],
    iconAnchor: [16, 16]
  });

  private redIcon = L.icon({
    iconUrl: 'icons/car-icon-red.png',
    iconSize: [32, 32],
    iconAnchor: [16, 16]
  });

  constructor(private mapService: MapService) {}

  setRoute(): void {
    if (!this.pickupAddress?.trim() || !this.destinationAddress?.trim()) return;
    // geocode pickup
    this.mapService.search(this.pickupAddress).subscribe((p: any) => {
      if (!p?.length) return;

      const pickupLatitude = +p[0].lat; //takes the first result(because it expects array of results)p[0]
      const pickupLongitude = +p[0].lon;
      // geocode destination
      this.mapService.search(this.destinationAddress).subscribe((d: any) => {
        if (!d?.length) return;

        const destinationLatitude = +d[0].lat;
        const destinationLongitude = +d[0].lon;

        this.drawRoute(pickupLatitude, pickupLongitude, destinationLatitude, destinationLongitude);
      });
    });
  }

  private drawRoute(
    pickupLatitude: number,
    pickupLongitude: number,
    destinationLatitude: number,
    destinationLongitude: number
  ) {
    // remove previous route if any
    if (this.routeControl) {
      this.map.removeControl(this.routeControl);
      this.routeControl = undefined;
    }

    this.routeControl = L.Routing.control({
      waypoints: [
        L.latLng(pickupLatitude, pickupLongitude),
        L.latLng(destinationLatitude, destinationLongitude),
      ],
      router: L.routing.mapbox(
        'pk.eyJ1IjoidG90YWxseS1zcGllczMzIiwiYSI6ImNtanpxYm54dzV1MTEzZnF4M3c4ejZ0c28ifQ.iwa5IGW8kqTBZtwXvVTQcQ',
        { profile: 'mapbox/driving' }
      ),
      fitSelectedRoutes: false,
      show: false,
      addWaypoints: false,
      routeWhileDragging: false,
    }).addTo(this.map);

    this.routeControl.on('routesfound', (e: any) => {
      const route = e.routes[0];
      //creating a rectangle around the route
      const bounds = L.latLngBounds(route.coordinates as any);
      //moves and zooms the map so the entire rectangle with route is visible
      this.map.fitBounds(bounds, { padding: [30, 30] });

      const summary = e.routes[0].summary;
      const distanceKm = Math.round((summary.totalDistance / 1000) * 10) / 10;
      const estimatedTime = Math.round(summary.totalTime / 60);

      this.routeInfo.emit({ distanceKm, estimatedTime });
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
    }).addTo(this.map);
  }
  //maybe fix to populate input fields with user clicked addresses
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
      //alert(mp.getLatLng());
    });
  }
  ngOnChanges(changes: SimpleChanges): void {
    if (!this.map) return; // map not created yet

    const pickupChanged = !!changes['pickupAddress'];
    const destinationChanged = !!changes['destinationAddress'];

    if (!pickupChanged && !destinationChanged) return;

    if (!this.pickupAddress.trim() || !this.destinationAddress.trim()) return;

    this.setRoute();
  }

  ngOnDestroy(): void {
    this.pollingSubscription?.unsubscribe();
    // we dont want to use up all resources when he is not on that page
  }

  ngAfterViewInit(): void {
    let DefaultIcon = L.icon({
      iconUrl: 'icons/map-icons/map-marker-icon.png',
      iconSize: [25, 25],
    });

    L.Marker.prototype.options.icon = DefaultIcon;
    this.initMap();
    this.startPollingPositions();
    //this.setRoute();
    // if inputs are already set by the time map is ready, draw route now
    if (this.pickupAddress.trim() && this.destinationAddress.trim()) {
      this.setRoute();
    }
    //this.registerOnClick();
  }

  private startPollingPositions(): void {
    
    this.pollingSubscription = interval(10000)
      .pipe(
        startWith(0),
        switchMap(() => this.mapService.getAllVehiclePositions())
      )
      .subscribe({
        next: (vehicles: any[]) => {
          this.updateVehicleMarkers(vehicles);
          console.log("nema")
        },
        error: (err) => console.error('error with getting a position:', err)
      });
  }

  private updateVehicleMarkers(vehicles: any[]): void {
  vehicles.forEach(v => {
    const latLng = L.latLng(v.currentLat, v.currentLng);
    const iconToUse = v.busy ? this.redIcon : this.greenIcon;

    if (this.vehicleMarkers.has(v.id)) {
      const marker = this.vehicleMarkers.get(v.id)!;
      marker.setLatLng(latLng);
      marker.setIcon(iconToUse); // update icon if the sttaus is updated
    } else {
      const marker = L.marker(latLng, { icon: iconToUse })
        .addTo(this.map)
        .bindPopup(`vehicle: ${v.id} <br> status: ${v.busy ? 'occupied' : 'free'}`);
      
      this.vehicleMarkers.set(v.id, marker);
    }
  });
}
}
