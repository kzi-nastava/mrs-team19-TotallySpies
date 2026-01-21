import { Component, Input } from '@angular/core';
import {Ride} from '../../models/ride.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ride-info',
  imports: [CommonModule],
  templateUrl: './ride-info.component.html',
  styleUrl: './ride-info.component.css',
})
export class RideInfoComponent {
  @Input() ride!: Ride;
  @Input() isUpcoming: boolean = false;
}
