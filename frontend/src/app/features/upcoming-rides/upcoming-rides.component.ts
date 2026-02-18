import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { PassengerUpcomingRideDTO } from '../../shared/models/ride.model';
import { RideService } from '../../shared/services/ride.service';
import { CommonModule } from '@angular/common';
import { UpcomingRideCardComponent } from '../../shared/components/upcoming-ride-card/upcoming-ride-card.component';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-upcoming-rides',
  imports: [CommonModule,  UpcomingRideCardComponent],
  templateUrl: './upcoming-rides.component.html',
  styleUrl: './upcoming-rides.component.css',
})
export class UpcomingRidesComponent implements OnInit{
  upcomingRides: PassengerUpcomingRideDTO[] = [];
  loading = false;

  constructor(private rideService: RideService, private cdr: ChangeDetectorRef) {}

  ngOnInit() {
    this.loading = true;

    this.rideService.getPassengerUpcomingRides()
      .pipe(
        finalize(() => {
          this.loading = false; 
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (res) => {
          console.log('Rides:', res);
          this.upcomingRides = res;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error:', err);
        }
      });
  }
}