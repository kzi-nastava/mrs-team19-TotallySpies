import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-driver-info',
  imports: [CommonModule],
  templateUrl: './driver-info.component.html',
  styleUrl: './driver-info.component.css',
  standalone: true,
})
export class DriverInfoComponent {
  @Input() driverName: string = 'Dimitrije Antic';
  @Input() carModel: string = 'Subaru XV';
  @Input() rating: number = 5;
  @Input() profileImageUrl: string = '/images/driver_placeholder.png';
}
