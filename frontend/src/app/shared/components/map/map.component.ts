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
import { RideTrackingDTO } from '../../models/ride.model';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
export type RouteInfo = { distanceKm: number; estimatedTime: number };

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css'],
  standalone: true,
})
export class MapComponent implements AfterViewInit, OnDestroy, OnDestroy {
  private map!: L.Map;
  private routeControl?: L.Routing.Control;
  private vehicleMarkers: Map<number, L.Marker> = new Map();
  private stompClient: Client | null = null;
  private coordinates: any[] = [];
  @Input() pickupAddress = '';
  @Input() destinationAddress = '';
  @Input() rideId: number | null = null;
  @Output() routeInfo = new EventEmitter<RouteInfo>();
  @Output() rideUpdate = new EventEmitter<RideTrackingDTO>();
  @Output() routeCoordinatesFound = new EventEmitter<any[]>();  // emitt coordinates of route when found

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

  private subscribeToWebsocket(): void {
    const socket = new SockJS('http://localhost:8080/ws-transport');
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000, // try again if connection is lost
      onConnect: () => {
        console.log('Connected to WebSocket for Map Updates');
        
        // if we are tracking specific ride
        if (this.rideId) {
          console.log(`Subscribing to specific ride: ${this.rideId}`);
          this.stompClient!.subscribe(`/topic/ride/${this.rideId}`, (message) => {
            const data: RideTrackingDTO = JSON.parse(message.body);
            this.handleSingleRideUpdate(data);
          });
        } 
        // all vehicles map
        else {
          console.log('Subscribing to all map updates');
          this.stompClient!.subscribe('/topic/vehicle-locations', (message) => {
            const vehicles = JSON.parse(message.body);
            this.updateVehicleMarkers(vehicles);
          });
        }
      }
    });
    this.stompClient.activate();
  }

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
      this.coordinates = route.coordinates; 
      this.routeCoordinatesFound.emit(this.coordinates);
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
    if (this.map && (changes['pickupAddress'] || changes['destinationAddress'])) {
        this.setRoute();
    }
  }

  ngOnDestroy(): void {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
    // we dont want to use up all resources when he is not on that page
  }

  ngAfterViewInit(): void {
    let DefaultIcon = L.icon({
      iconUrl: 'icons/map-icons/map-marker-icon.png',
      iconSize: [25, 25],
    });

    L.Marker.prototype.options.icon = DefaultIcon;
    this.initMap();
    if (this.pickupAddress.trim() && this.destinationAddress.trim()) {
      this.setRoute();
    }
    this.subscribeToWebsocket();
    // if inputs are already set by the time map is ready, draw route now
    //this.registerOnClick();
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

// update map for specific ride tracking
  private handleSingleRideUpdate(data: RideTrackingDTO): void {
    this.rideUpdate.emit(data);

    const latLng = L.latLng(data.vehicleLat, data.vehicleLng);

    if (this.vehicleMarkers.has(data.rideId)) {
      const marker = this.vehicleMarkers.get(data.rideId)!;
      marker.setLatLng(latLng);
    } else {
      this.vehicleMarkers.forEach(m => m.remove());
      this.vehicleMarkers.clear();

      const marker = L.marker(latLng, { icon: this.redIcon })
        .addTo(this.map)
        .bindPopup(`Vehicle arrived in: ${data.eta} min`);
      
      this.vehicleMarkers.set(data.rideId, marker);
    }
  }

}
