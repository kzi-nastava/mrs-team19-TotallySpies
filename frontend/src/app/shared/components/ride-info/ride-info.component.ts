import { Component, Input } from '@angular/core';
import {Ride, RideFinishResponseDTO} from '../../models/ride.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ride-info',
  imports: [CommonModule],
  templateUrl: './ride-info.component.html',
  styleUrl: './ride-info.component.css',
})
export class RideInfoComponent {
  @Input() ride!: Ride | RideFinishResponseDTO;
  @Input() isUpcoming: boolean = false;

  // helper function to see if its is dto or not
  isDTO(r: any): r is RideFinishResponseDTO {
    return r && 'rideId' in r;
  }
}
