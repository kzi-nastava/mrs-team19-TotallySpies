import { Component, Input, SimpleChanges } from '@angular/core';
import { PassengerUpcomingRideDTO, RideStopDTO } from '../../models/ride.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upcoming-ride-card',
  imports: [CommonModule],
  templateUrl: './upcoming-ride-card.component.html',
  styleUrl: './upcoming-ride-card.component.css',
})
export class UpcomingRideCardComponent {
  @Input() ride!: PassengerUpcomingRideDTO;

  orderedStops: RideStopDTO[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['ride'] && this.ride && this.ride.locations) {
      this.orderedStops = [...this.ride.locations].sort(
        (a, b) => a.orderIndex - b.orderIndex
      );
    }
  }
}
