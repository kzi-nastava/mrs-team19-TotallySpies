import { Component } from '@angular/core';
import { Ride } from '../../shared/models/ride.model';
import { RideInfoComponent } from '../../shared/components/ride-info/ride-info.component';
import { PassengerInfoComponent } from '../../shared/components/passenger-info/passenger-info.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-ride-history',
imports: [CommonModule, RideInfoComponent, PassengerInfoComponent],  templateUrl: './ride-history.component.html',
  styleUrl: './ride-history.component.css',
})

export class RideHistoryComponent {
  // rides: Ride[] = [
  //   {
  //     date: '15.12.2025.',
  //     startTime: '1:34PM',
  //     endTime: '1:47AM',
  //     price: 590,
  //     startLocation: 'Micurinova 44',
  //     endLocation: 'Strazilovska 37',
  //     status: 'Cancelled', 
  //     cancelledBy: 'Passenger',
  //     passengers: [
  //       { name: 'Sara Trajkovic', imageUrl: '/images/sara.jpg.avif' },
  //       { name: 'Marija Lisac', imageUrl: '/images/marija.jpg.avif' },
  //       { name: 'Balsa Vuletic', imageUrl: '/images/balsa.jpg.avif' }
  //     ],
  //     panicPressed: true,
  //     isUpcoming: false,
  //   },
  //   {
  //     date: '16.12.2025.',
  //     startTime: '07:54AM',
  //     endTime: '08:01AM',
  //     price: 470,
  //     startLocation: 'Rumenacka 40',
  //     endLocation: 'Bulevar Oslobodjenja 104',
  //     status: 'Finished',
  //     passengers: [
  //       { name: 'Igor Lazic', imageUrl: '/images/igor.jpg.avif' }
  //     ],
  //     panicPressed: false,
  //     isUpcoming: false,
  //   }
  // ];
}