import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RideInfoComponent } from '../../shared/components/ride-info/ride-info.component';
import { PassengerInfoComponent } from '../../shared/components/passenger-info/passenger-info.component';
import { RideFinishResponseDTO, RideDetailsDTO } from '../../shared/models/ride.model';
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

  mapFinishToDetails(finish: RideFinishResponseDTO): RideDetailsDTO {
  return {
    rideId: finish.rideId,
    status: finish.status as 'ACTIVE' | 'SCHEDULED' | 'COMPLETED',
    startLocation: finish.startLocation,
    endLocation: finish.endLocation,
    startTime: finish.displayTime,
    price: finish.totalPrice,
    passengers: finish.passengers,
    nextRideId: finish.nextRideId,
  };
}

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
