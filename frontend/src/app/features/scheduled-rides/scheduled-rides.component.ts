import { Component } from '@angular/core';
import { RideInfoComponent } from '../../shared/components/ride-info/ride-info.component';
import { PassengerInfoComponent } from '../../shared/components/passenger-info/passenger-info.component';
import { Ride } from '../../shared/models/ride.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-scheduled-rides',
  imports: [RideInfoComponent, PassengerInfoComponent, CommonModule],
  templateUrl: './scheduled-rides.component.html',
  styleUrl: './scheduled-rides.component.css',
  standalone: true,
})
export class ScheduledRidesComponent {
rides: Ride[] = [
    {
      date: '25.1.2026.',
      startTime: '1:34PM',
      endTime: '1:47PM',
      price: 590,
      startLocation: 'Micurinova 44',
      endLocation: 'Strazilovska 37',
      status: 'Upcoming', 
      passengers: [
        { name: 'Sara Trajkovic', imageUrl: '/images/sara.jpg.avif' },
        { name: 'Marija Lisac', imageUrl: '/images/marija.jpg.avif' },
        { name: 'Balsa Vuletic', imageUrl: '/images/balsa.jpg.avif' }
      ],
      panicPressed: false,
      isUpcoming: true,
    },
    {
      date: '27.1.2026.',
      startTime: '07:54AM',
      endTime: '08:01AM',
      price: 470,
      startLocation: 'Rumenacka 40',
      endLocation: 'Bulevar Oslobodjenja 104',
      status: 'Upcoming',
      passengers: [
        { name: 'Igor Lazic', imageUrl: '/images/igor.jpg.avif' }
      ],
      panicPressed: false,
      isUpcoming: true,
    }
  ];
}
