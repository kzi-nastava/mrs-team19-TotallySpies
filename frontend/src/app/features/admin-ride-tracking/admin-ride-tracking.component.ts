import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RideService } from '../../shared/services/ride.service';
import { ActiveRideDTO, RideDetailsDTO } from '../../shared/models/ride.model';
import { RideInfoComponent } from '../../shared/components/ride-info/ride-info.component';
import { PassengerInfoComponent } from '../../shared/components/passenger-info/passenger-info.component';
import { MapComponent } from '../../shared/components/map/map.component';
import { DriverInfoComponent } from '../../shared/components/driver-info/driver-info.component';

@Component({
  selector: 'app-admin-ride-tracking',
  standalone: true,
  imports: [
    CommonModule, 
    FormsModule,         // Dodaj za ngModel u search
    RideInfoComponent, 
    PassengerInfoComponent, 
    MapComponent,
    DriverInfoComponent
  ],
  templateUrl: './admin-ride-tracking.component.html',
  styleUrls: ['./admin-ride-tracking.component.css']
})
export class AdminRideTrackingComponent implements OnInit {

  activeRides: RideDetailsDTO[] = [];
  searchDriver: string = '';

  constructor(private rideService: RideService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadRides();
  }

  loadRides(): void {
    this.rideService.getActiveRidesForAdmin(this.searchDriver).subscribe({
      next: (historyDtos: ActiveRideDTO[]) => {
        this.activeRides = historyDtos.map(dto => this.mapHistoryToDetails(dto));
        this.cdr.detectChanges();
      },
      error: (err) => console.error("Error loading rides:", err)
    });
  }

  onSearch(event: any): void {
    this.searchDriver = event.target.value;
    this.loadRides();
  }

  private mapHistoryToDetails(dto: ActiveRideDTO): RideDetailsDTO {
    return {
      rideId: dto.id,
      status: dto.status as any, 
      startLocation: dto.startLocation,
      endLocation: dto.endLocation,
      startTime: dto.startTime,
      endTime: dto.endTime,
      price: dto.price,
      driverName: dto.driverName,
      driverImage: dto.driverPicture, 
      carModel: dto.vehicleModel,
      driverRating: dto.driverAverageRating,
      panicPressed: dto.panicPressed,
      panicReason: dto.panicReason,
      passengers: dto.passengers ? dto.passengers.map(name => ({
        id: 0, 
        name: name,
        lastName: '',
        email: '',
        password: '',
        address: '',
        role: 'PASSENGER',
        phoneNumber: '',
        profilePicture: 'icons/person.png'
      })) : []
    };
  }
}