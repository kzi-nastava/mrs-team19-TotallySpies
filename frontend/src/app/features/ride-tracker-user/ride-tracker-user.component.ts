import { Component } from '@angular/core';
import { DriverInfoComponent } from '../../shared/components/driver-info/driver-info.component';
import { TimerCardComponent } from '../../shared/components/timer-card/timer-card.component';
import { MapComponent } from '../../shared/components/map/map.component';

@Component({
  selector: 'app-ride-tracker-user',
  imports: [DriverInfoComponent, TimerCardComponent, MapComponent],
  templateUrl: './ride-tracker-user.component.html',
  styleUrl: './ride-tracker-user.component.css',
})
export class RideTrackerUserComponent {
  currentDriver = {
    driverName: 'Dimitrije Antic',
    carModel: 'Subaru XV',
    rating: 5,
    profileImageUrl: '/images/driver_placeholder.png' 
  };

  minutesRemaining: number = 7;
  progress: number = 70;
  pickupAddress: string = 'Micurinova 40, Novi Sad';
  destinationAddress: string = 'Strazilovska 7, Novi Sad';

}
