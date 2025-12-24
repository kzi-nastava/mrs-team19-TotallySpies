import { Component, Input } from '@angular/core';
import { Passenger } from '../../models/ride.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-passenger-info',
  imports: [CommonModule],
  templateUrl: './passenger-info.component.html',
  styleUrl: './passenger-info.component.css',
})
export class PassengerInfoComponent {
  @Input() passengers: Passenger[] = [];
}
