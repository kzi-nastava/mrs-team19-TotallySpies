import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RideService } from '../../shared/services/ride.service';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { MapComponent } from '../../shared/components/map/map.component';
import { RideDetailsStopDTO } from '../../shared/models/ride.model';
import { PassengerRideDetailsResponseDTO } from '../../shared/models/passenger-ride-details.model';
import { RideGradeDTO } from '../../shared/models/ride-grade.model';

@Component({
  selector: 'app-ride-details',
  imports: [CommonModule, MapComponent],
  templateUrl: './ride-details.component.html',
  styleUrl: './ride-details.component.css',
})
export class RideDetailsComponent implements OnInit{
  constructor(private rideService : RideService, private route : ActivatedRoute,private cdr: ChangeDetectorRef){}
  rideStops : RideDetailsStopDTO[] = [];
  driverName : string = '';
  driverLastName : string = '';
  driverEmail : string = '';
  driverPhoneNumber : string = '';
  distanceKm : number = 0;
  totalPrice : number = 0;
  reportReasons : Record<string,string> = {}
  //rideGrades : Record<string,number> = {}
  rideGrades : RideGradeDTO[] = [];
  

  ngOnInit(): void {
    const rideId = Number(this.route.snapshot.paramMap.get('id'));
    this.rideService.getRideDetails(rideId).subscribe({
      next: (response) => {
        this.rideStops = [...response.rideStops];
        this.driverName = response.driverName,
        this.driverLastName = response.driverLastName,
        this.driverEmail = response.driverEmail,
        this.driverPhoneNumber = response.driverPhoneNumber,
        this.distanceKm = response.distanceKm,
        this.totalPrice = response.totalPrice,
        this.reportReasons = response.reportReasons,
        this.rideGrades = response.rideGrades,
        this.cdr.detectChanges();
        console.log(response);
      },
      error: (err) => console.error(err),
    });
  }
  
   
       
}
