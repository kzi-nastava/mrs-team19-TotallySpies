import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { DriverInfoComponent } from '../../shared/components/driver-info/driver-info.component';
import { TimerCardComponent } from '../../shared/components/timer-card/timer-card.component';
import { MapComponent } from '../../shared/components/map/map.component';
import { CommonModule } from '@angular/common';
import { interval, Subscription, switchMap } from 'rxjs';
import { RideTrackingDTO } from '../../shared/models/ride.model';
import { RideService } from '../../shared/services/ride.service';
import { ActivatedRoute } from '@angular/router';
import { PanicRideDTO } from '../../shared/models/panic-ride.model';
import { MapService } from '../../shared/components/map/map.service';

@Component({
  selector: 'app-ride-tracker-user',
  imports: [DriverInfoComponent, TimerCardComponent, MapComponent, CommonModule,],
  templateUrl: './ride-tracker-user.component.html',
  styleUrl: './ride-tracker-user.component.css',
})
export class RideTrackerUserComponent implements OnInit, OnDestroy {
  private trackingSub?: Subscription;
  rideId: number = 123;
  minutesRemaining: number = 0;
  currentVehicleLocation: { lat: number; lng: number } = { lat: 0, lng: 0 };
  pickupAddress: string = '';
  destinationAddress: string = '';
  progress: number = 100;
  currentDriver = {
    driverName: '', 
    carModel: '',
    rating: 4.9,
    profileImageUrl: 'icons/person.png',
    isFinished: false
  };
  rideStatus: string = '';

  routeDistanceKm: number = 0;
  vehicleType: string = 'STANDARD'; // set from backend later!!


  constructor(
    private rideService: RideService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
    private mapService : MapService
  ) {}


  ngOnInit() {
    const idParam = this.route.snapshot.paramMap.get('id');
    
    if (idParam) {
      this.rideId = Number(idParam);
    } else {
      console.warn('ID nije u URL-u, koristim testni ID 2');
      this.rideId = 1;
    }
    
    this.loadInitialData();
  }

  loadInitialData() {
    this.rideService.getRideLocation(this.rideId).subscribe({
      next: (data: RideTrackingDTO) => {
        this.updateFields(data);
        
        if (data.status === 'ACTIVE') {
          this.startTracking();
        }
      },
      error: (err) => {
        console.error('Greška pri učitavanju vožnje:', err);
      }
    });
  }

  updateFields(data: RideTrackingDTO) {
    if (!data) return; 

    this.rideStatus = data.status;
    this.minutesRemaining = data.eta;
    this.currentVehicleLocation = { lat: data.vehicleLat, lng: data.vehicleLng };
    this.pickupAddress = data.pickupAddress || 'Adresa nije dostupna';
    this.destinationAddress = data.destinationAddress || 'Adresa nije dostupna';
    
    const isCompleted = data.status === 'COMPLETED' || data.status === 'STOPPED';
    
    this.currentDriver = {
      driverName: data.driverName,
      carModel: data.carModel,
      rating: 4.9,
      profileImageUrl: data.profilePicture,
      isFinished: isCompleted
    };

    if (data.status === 'ACTIVE') {
      this.progress = data.eta > 0 ? (100 - (data.eta * 2)) : 100; 
      if (this.progress < 0) this.progress = 5;
    } else {
      this.progress = 100;
      this.minutesRemaining = 0;
    }

    this.cdr.detectChanges();
  }

  startTracking() {
    if (this.trackingSub) {
      return;
    }
    
    this.trackingSub = interval(5000).pipe(
      switchMap(() => this.rideService.getRideLocation(this.rideId))
    ).subscribe({
      next: (data: RideTrackingDTO) => {
        this.updateFields(data);
        
        if (data.status === 'COMPLETED' || data.status === 'STOPPED') {
          this.stopTracking();
        }
      },
      error: (err) => {
        console.error('Tracking error:', err);
        this.stopTracking();
      }
    });
  }

  stopTracking() {
    this.trackingSub?.unsubscribe();
    this.trackingSub = undefined;
  }

  ngOnDestroy() {
    this.stopTracking();
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
        time : new Date().toISOString()
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
    const stopLat = this.currentVehicleLocation.lat;
    const stopLng = this.currentVehicleLocation.lng;

    if (!stopLat || !stopLng) {
      alert('Current vehicle location not available yet.');
      return;
    }

    if (!this.pickupAddress || !this.pickupAddress.trim()) {
      alert('Pickup address is missing.');
      return;
    }

    //pickup address -> coordinates
    this.mapService.search(this.pickupAddress.trim()).subscribe({
      next: (pickupResults: any) => {
        if (!pickupResults?.length) {
          alert('Could not geocode pickup address.');
          return;
        }

        const pickupLat = Number(pickupResults[0].lat);
        const pickupLng = Number(pickupResults[0].lon);

        // route distance from pickup -> current stop point
        this.mapService.getRouteInfo(pickupLat, pickupLng, stopLat, stopLng).subscribe({
          next: (routeInfo) => {
            if (!routeInfo.distanceKm || routeInfo.distanceKm <= 0) {
              alert('Could not calculate driven distance.');
              return;
            }
            // vehicleType: ideally comes from backend; if you don’t have it, default STANDARD
            const vehicleType = (this as any).rideVehicleType ?? 'STANDARD';
            const newTotalPrice = this.calcTotalPrice(vehicleType, routeInfo.distanceKm);
            const newEndTime = new Date().toISOString();

            // 3) stop coords -> address
            this.mapService.reverseSearch(stopLat, stopLng).subscribe({
              next: (rev: any) => {
                let address = (rev?.display_name ?? '').trim();
                if (!address) {
                  const typed = prompt('Could not detect stop address. Enter stop address:');
                  if (!typed || typed.trim().length === 0) {
                    alert('Stop address is required.');
                    return;
                  }
                  address = typed.trim();
                }

                const dto = {
                  rideId: this.rideId,
                  newEndTime,
                  newTotalPrice,
                  newDestination: {
                    address,
                    lat: stopLat,
                    lng: stopLng
                  }
                };

                this.rideService.stopRide(dto).subscribe({
                  next: (msg) => {
                    alert(msg);          // "Ride stopped!"
                    this.stopTracking(); // stop polling
                    this.loadInitialData();
                  },
                  error: (err) => {
                    console.error(err);
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
            console.error('Route distance failed:', err);
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