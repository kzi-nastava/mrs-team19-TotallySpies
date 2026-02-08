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

  getProfileImageUrl(p: any): string {
const defaultUrl = `icons/person.png`;

  if (!p?.profilePicture) return defaultUrl;

  // trim i lowercase da pokrijemo sve varijante
  const pic = p.profilePicture.trim().toLowerCase();

  // ako backend vraća "[null]", "null" ili prazan string
  if (!pic || pic === 'null' || pic === '[null]') return defaultUrl;

  // normalan fileName
  const fileName = pic.split('/').pop();
  return fileName ? `http://localhost:8080/api/v1/users/image/${fileName}` : defaultUrl;
}
}
