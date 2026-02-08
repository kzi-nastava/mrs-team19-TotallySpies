import { Component, OnInit } from '@angular/core';
import { DriverRideHistoryDTO, RideDetailsDTO } from '../../shared/models/ride.model';
import { RideInfoComponent } from '../../shared/components/ride-info/ride-info.component';
import { PassengerInfoComponent } from '../../shared/components/passenger-info/passenger-info.component';
import { CommonModule } from '@angular/common';
import { DriverService } from '../../shared/services/driver.service';
import { AuthService } from '../../core/auth/services/auth.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-ride-history',
  imports: [CommonModule, RideInfoComponent, PassengerInfoComponent, FormsModule],
  templateUrl: './ride-history.component.html',
  styleUrl: './ride-history.component.css',
})

export class RideHistoryComponent implements OnInit{
  
  isLoading: boolean = true;

  mapHistoryToDetails(history: DriverRideHistoryDTO): RideDetailsDTO {
    return {
      rideId: history.id,
      status: history.cancelled ? 'STOPPED' : 'COMPLETED',
      startLocation: history.startLocation,
      endLocation: history.endLocation,
      startTime: history.startTime,
      endTime: history.endTime,
      price: history.price,
      cancelled: history.cancelled,
      cancelledBy: history.cancelledBy,
      cancellReason: history.cancellReason,
      panicPressed: history.panicPressed,
      panicReason: history.panicReason
    };
  }

  mapToPassengerObjects(names: string[]): any[] {
    if (!names) return [];
    return names.map(name => ({
      name: name,
      profilePicture: null
    }));
  }

  rides: DriverRideHistoryDTO[] = [];
  filterStart: string = '';
  filterEnd: string = '';

  driverId: number = 0;

  constructor(private driverService: DriverService,
    private authService: AuthService
  ){}

  ngOnInit(): void {
    const today = new Date();
    const monthAgo = new Date();
    monthAgo.setDate(today.getDate() - 90);

    // format YYYY-MM-DD
    this.filterEnd = today.toISOString().split('T')[0];
    this.filterStart = monthAgo.toISOString().split('T')[0];
    this.loadHistory();
  }

  loadHistory(): void {
    this.isLoading = true;
    this.driverService.getDriverHistory(this.filterStart, this.filterEnd)
      .subscribe({
        next: (data) => {
          this.rides = data;
          this.isLoading = false;
          // the newest rides on top
          this.rides.sort((a, b) => new Date(b.startTime).getTime() - new Date(a.startTime).getTime());
        },
        error: (err) => {
          this.isLoading = false;
          console.error("error while loading", err);
        }
      });
  }

  resetFilters(): void {
    this.filterStart = '';
    this.filterEnd = '';
    this.loadHistory();
  }
}