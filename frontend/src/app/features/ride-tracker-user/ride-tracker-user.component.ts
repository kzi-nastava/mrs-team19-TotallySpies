import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { DriverInfoComponent } from '../../shared/components/driver-info/driver-info.component';
import { TimerCardComponent } from '../../shared/components/timer-card/timer-card.component';
import { MapComponent } from '../../shared/components/map/map.component';
import { CommonModule } from '@angular/common';
import { interval, Subscription, switchMap } from 'rxjs';
import { RideTrackingDTO } from '../../shared/models/ride.model';
import { RideService } from '../../shared/services/ride.service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-ride-tracker-user',
  imports: [DriverInfoComponent, TimerCardComponent, MapComponent, CommonModule],
  templateUrl: './ride-tracker-user.component.html',
  styleUrl: './ride-tracker-user.component.css',
})
export class RideTrackerUserComponent implements OnInit, OnDestroy {
  private trackingSub?: Subscription;
  rideId: number = 123;
  minutesRemaining: number = 0;
  currentVehicleLocation: { lat: number; lng: number } = { lat: 0, lng: 0 };
  pickupAddress: string = 'Micurinova 38, Novi Sad'; 
  destinationAddress: string = 'Bulevar Oslobodjenja 104, Novi Sad';
  progress: number = 100;
  currentDriver = {
    driverName: 'Dimitrije Antić', 
    carModel: 'Subaru XV',
    rating: 4.9,
    profileImageUrl: 'icons/person.png',
    isFinished: false
  };

  constructor(
  private rideService: RideService,
  private route: ActivatedRoute,
  private cdr: ChangeDetectorRef
) {}

  ngOnInit() {
  const idParam = this.route.snapshot.paramMap.get('id');
  
  if (idParam) {
    this.rideId = Number(idParam);
  } else {
    console.warn('ID nije u URL-u, koristim testni ID 2');
    this.rideId = 2;
  }
  
  this.loadInitialData();
  this.startTracking();
}

  loadInitialData() {
  this.rideService.getRideLocation(this.rideId).subscribe(data => {
    this.updateFields(data);
  });
  }

  updateFields(data: RideTrackingDTO) {
  if (!data) return; 

  this.minutesRemaining = data.eta;
  this.currentVehicleLocation = { lat: data.vehicleLat, lng: data.vehicleLng };
  this.pickupAddress = data.pickupAddress || 'no address';
  this.destinationAddress = data.destinationAddress || 'no address';
  
  this.currentDriver.driverName = data.driverName;
  this.currentDriver.carModel = data.carModel;
  this.currentDriver.profileImageUrl = data.profilePicture;

  this.progress = data.eta > 0 ? (100 - (data.eta * 2)) : 100; 
  if (this.progress < 0) this.progress = 5;

  if (data.status === 'COMPLETED') {
    this.currentDriver = { 
      ...this.currentDriver, 
      isFinished: true 
    };
    
    this.minutesRemaining = 0;
    this.progress = 100;
    this.stopTracking();
  }

  this.cdr.detectChanges();
}

  startTracking() {
  this.trackingSub = interval(5000).pipe(
    switchMap(() => this.rideService.getRideLocation(this.rideId))
  ).subscribe({
    next: (data: RideTrackingDTO) => {
      this.minutesRemaining = data.eta;
      this.currentVehicleLocation = { lat: data.vehicleLat, lng: data.vehicleLng };
      this.pickupAddress = data.pickupAddress || this.pickupAddress;
      this.destinationAddress = data.destinationAddress || this.destinationAddress;
      
      this.currentDriver = {
        ...this.currentDriver,
        driverName: data.driverName,
        carModel: data.carModel,
        profileImageUrl: data.profilePicture,
        isFinished: data.status === 'COMPLETED'
      };
      
      if (data.status === 'COMPLETED') {
        this.stopTracking();
      }
      
      this.cdr.detectChanges(); 
    },
    error: (err) => console.error('Tracking error:', err)
  });
}

  stopTracking() {
    this.trackingSub?.unsubscribe();
  }

  ngOnDestroy() {
    this.stopTracking();
  }
}
