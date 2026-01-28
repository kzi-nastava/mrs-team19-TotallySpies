import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RideInfoComponent } from '../../shared/components/ride-info/ride-info.component';
import { PassengerInfoComponent } from '../../shared/components/passenger-info/passenger-info.component';
import { RideFinishResponseDTO } from '../../shared/models/ride.model';
import { CommonModule } from '@angular/common';
import { RideService } from '../../shared/services/ride.service';
import { CancelRideDTO } from '../../shared/models/cancel-ride.model';

@Component({
  selector: 'app-scheduled-rides',
  imports: [RideInfoComponent, PassengerInfoComponent, CommonModule],
  templateUrl: './scheduled-rides.component.html',
  styleUrl: './scheduled-rides.component.css',
  standalone: true,
})
export class ScheduledRidesComponent implements OnInit{
  rides: RideFinishResponseDTO[] = [];
  constructor(private rideService: RideService, 
    private cdr: ChangeDetectorRef
  ) {}
  ngOnInit() {
    this.loadRides();
  }

  loadRides() {
  this.rideService.getRidesForDriver().subscribe({
    next: (data) => {
      console.log('Podaci stigli:', data);
      this.rides = data;
      this.cdr.detectChanges();
    },
    error: (error) => {
      console.error('Greška:', error);
    }
  });
}

formatDate(dateString: string): string {
  const date = new Date(dateString);
  return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

  handleFinish(id: number) {
    this.rideService.finishRide(id).subscribe(() => {
      alert("Ride finished! Email sent.");
      this.loadRides();
    });
  }

  cancelRide(cancelRideDTO : CancelRideDTO) {
  this.rideService.cancelRide(cancelRideDTO).subscribe({
    next: (data) => {
      alert('Ride cancelled successfully.');
      this.loadRides();
    },
    // error: (err) => {
    //   console.error(err);
    //   alert('Failed to cancel ride.');
    // }
  });
  }

  openCancelDialog(rideId: number) {
    const reason = prompt('Enter cancel reason:');

    if (!reason || reason.trim().length === 0) {
      alert('Cancel reason is required.');
      return;
    }
    const cancelRideDTO : CancelRideDTO = {
      rideId : rideId,
      rejectionReason : reason,
      time : new Date().toISOString()
    };

    this.cancelRide(cancelRideDTO);
  }

  handleStart(rideId: number) {
  this.rideService.startRide(rideId).subscribe({
    next: () => {
      alert('Ride successfully started!');
      this.loadRides(); // refresh status
    },
    error: (err) => {
      console.error(err);
      alert('Failed to start ride.');
    }
  });
}

}
