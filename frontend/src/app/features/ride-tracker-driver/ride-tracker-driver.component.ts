import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { DriverInfoComponent } from '../../shared/components/driver-info/driver-info.component';
import { TimerCardComponent } from '../../shared/components/timer-card/timer-card.component';
import { MapComponent } from '../../shared/components/map/map.component';
import { CommonModule } from '@angular/common';
import { RideTrackingDTO } from '../../shared/models/ride.model';
import { RideService } from '../../shared/services/ride.service';
import { ActivatedRoute, Router } from '@angular/router';
import { PanicRideDTO } from '../../shared/models/panic-ride.model';
import { MapService } from '../../shared/components/map/map.service';
import { MatDialog } from '@angular/material/dialog';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { AuthService } from '../../core/auth/services/auth.service';
import { StopRideDTO } from '../../shared/models/stop-ride.model';
@Component({
  selector: 'app-ride-tracker-driver',
  imports: [DriverInfoComponent, TimerCardComponent, MapComponent, CommonModule,],
  templateUrl: './ride-tracker-driver.component.html',
  styleUrl: './ride-tracker-driver.component.css',
})
export class RideTrackerDriverComponent implements OnInit, OnDestroy {
  private stompClient: Client | null = null;
  rideId!: number;
  minutesRemaining: number = 0;
  currentVehicleLocation: { lat: number; lng: number } = { lat: 0, lng: 0 };
  pickupAddress: string = '';
  destinationAddress: string = '';
  progress: number = 0;
  currentDriver = {
    driverName: '', 
    carModel: '',
    rating: 0.0,
    profileImageUrl: 'icons/person.png',
    isFinished: false
  };
  rideStatus: string = '';
  routeDistanceKm: number = 0;
  vehicleType: string = 'STANDARD'; // set from backend later!!


  constructor(
    private rideService: RideService,
    private route: ActivatedRoute,
    private mapService : MapService,
    private dialog: MatDialog,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
    private router: Router
  ) {}


  ngOnInit() {
    this.route.params.subscribe(params => {
      this.rideId = +params['id'];
      if (!this.authService.isLoggedIn()) {
      alert("Please login to track your ride.");
      this.router.navigate(['/login'], { queryParams: { returnUrl: '/ride-tracker-driver/' + this.rideId } });
      return;
    }
      this.loadInitialData();
      this.connectToWebSocket(); 
    });
  }

  private loadInitialData(): void {
    this.mapService.getRideLocation(this.rideId).subscribe({
      next: (data: RideTrackingDTO) => this.handleRideUpdate(data),
      error: (err) => console.error('Initial fetch failed:', err)
    });
  }

  private connectToWebSocket(): void {
    const socket = new SockJS('http://localhost:8080/ws-transport');
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('Connected to Ride Tracking WebSocket');
        
        this.stompClient!.subscribe(`/topic/ride/${this.rideId}`, (message) => {
          if (message.body) {
            const data: RideTrackingDTO = JSON.parse(message.body);
            this.handleRideUpdate(data);
          }
        });
      }
    });
    this.stompClient.activate();
  }

  handleRideUpdate(data: RideTrackingDTO): void {
    console.log("Ride status from back:", data.status);

    this.pickupAddress = data.pickupAddress;
    this.destinationAddress = data.destinationAddress;
    this.currentVehicleLocation = { lat: data.vehicleLat, lng: data.vehicleLng };
    
    this.currentDriver = {
        ...this.currentDriver,
        driverName: data.driverName,
        carModel: data.carModel,
        rating: data.rating,
        profileImageUrl: data.profilePicture || 'icons/person.png',
        isFinished: data.status === 'COMPLETED'
    };
    this.minutesRemaining = data.eta;
    
    if (data.status === 'ACTIVE') {
      this.progress = data.eta > 0 ? Math.max(5, 100 - (data.eta * 2)) : 100;
    } else if (data.status === 'COMPLETED') {
      this.progress = 100;
      this.currentDriver.isFinished = true;
      this.stompClient?.deactivate(); 
    }
    this.cdr.detectChanges();
  }

  ngOnDestroy() {
    if (this.stompClient) {
          this.stompClient.deactivate();
    }  
  }

  panicRide(panicRideDTO : PanicRideDTO) {
    this.rideService.panicRide(panicRideDTO).subscribe({
      next: (data) => {
        alert('PANIC sent successfully.');
      },
      // error: (err) => {
      //   console.error(err);
      //   alert('Failed to cancel ride.');
      // }
    });
    }
  openPanicDialog(rideId: number) {
      const reason = prompt('Enter PANIC reason:');
  
      if (!reason || reason.trim().length === 0) {
        alert('Panic reason is required.');
        return;
      }
      const cancelRideDTO : PanicRideDTO = {
        rideId : rideId,
        reason : reason,
      };
  
      this.panicRide(cancelRideDTO);
  }
  
  private basePriceByType(vehicleType: string): number {
    switch (vehicleType) {
      case 'STANDARD': return 200;  //CHANGE LATER
      case 'LUXURY': return 400;
      case 'VAN': return 300;
      default: return 200;
    }
  }

  private calcTotalPrice(vehicleType: string, distanceKm: number): number {
    return this.basePriceByType(vehicleType) + distanceKm * 120;
  }

  stopRide() {
    if (!this.rideId) return;

    const stopLat = this.currentVehicleLocation.lat;
    const stopLng = this.currentVehicleLocation.lng;
    if (!Number.isFinite(stopLat) || !Number.isFinite(stopLng) || (stopLat === 0 && stopLng === 0)) {
      alert('Current vehicle location not available yet.');
      return;
    }
    if (!this.pickupAddress?.trim()) {
      alert('Pickup address is missing.');
      return;
    }
    const newEndTime = new Date().toISOString().replace('Z', '');
    // 1) pickup address -> koordinate
    this.mapService.search(this.pickupAddress.trim()).subscribe({
      next: (pickupResults: any[]) => {
        if (!pickupResults?.length) {
          alert('Could not geocode pickup address.');
          return;
        }
        const pickupLat = Number(pickupResults[0].lat);
        const pickupLng = Number(pickupResults[0].lon);
        if (!Number.isFinite(pickupLat) || !Number.isFinite(pickupLng)) {
          alert('Invalid pickup coordinates.');
          return;
        }
        // 2) distanca pickup -> stop
        this.mapService.getRouteInfo(pickupLat, pickupLng, stopLat, stopLng).subscribe({
          next: (routeInfo) => {
            const distanceKm = routeInfo?.distanceKm ?? 0;
            if (distanceKm <= 0) {
              alert('Could not calculate driven distance.');
              return;
            }
            const newTotalPrice = this.calcTotalPrice(this.vehicleType ?? 'STANDARD', distanceKm);
            // 3) stop coords -> adresa
            this.mapService.reverseSearch(stopLat, stopLng).subscribe({
              next: (rev: any) => {
                const address = (rev?.display_name ?? '').trim();
                if (!address) {
                  alert('Could not resolve stop address.');
                  return;
                }
                const dto: StopRideDTO = {
                  rideId: this.rideId,
                  newEndTime,
                  newTotalPrice,
                  newDestination: {
                    address,
                    latitude: stopLat,
                    longitude: stopLng
                  }
                };

                this.rideService.stopRide(dto).subscribe({
                  next: (msg: string) => {
                    alert(msg || 'Ride stopped!');
                    this.currentDriver.isFinished = true;
                    this.stompClient?.deactivate();
                    this.cdr.detectChanges();
                  },
                  error: (err) => {
                    console.error('Stop ride failed:', err);
                    alert('Failed to stop ride.');
                  }
                });
              },
              error: (err) => {
                console.error('Reverse search failed:', err);
                alert('Failed to resolve stop address.');
              }
            });
          },
          error: (err) => {
            console.error('Route info failed:', err);
            alert('Failed to calculate driven distance.');
          }
        });
      },
      error: (err) => {
        console.error('Pickup geocode failed:', err);
        alert('Failed to geocode pickup address.');
      }
    });
  }

  

}