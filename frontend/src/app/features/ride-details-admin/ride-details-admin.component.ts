import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { MapComponent } from '../../shared/components/map/map.component';
import { RideService } from '../../shared/services/ride.service';
import { ActivatedRoute } from '@angular/router';
import { RideDetailsStopDTO } from '../../shared/models/ride.model';
import { RideGradeDTO } from '../../shared/models/ride-grade.model';
import { PassengerInfoDTO } from '../../shared/models/passenger-info.model';

@Component({
  selector: 'app-ride-details-admin',
  imports: [CommonModule, MapComponent],
  templateUrl: './ride-details-admin.component.html',
  styleUrl: './ride-details-admin.component.css',
})
export class RideDetailsAdminComponent {
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
  passengersInfo : PassengerInfoDTO[] = [];
  

  ngOnInit(): void {
    const rideId = Number(this.route.snapshot.paramMap.get('id'));
    this.rideService.getRideDetailsForAdmin(rideId).subscribe({
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
        this.passengersInfo = response.passengersInfo,
        this.cdr.detectChanges();
        console.log(response);
      },
      error: (err) => console.error(err),
    });
  }
  
   
       
}
