import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormsModule, NgModel, ReactiveFormsModule } from '@angular/forms';
import { UserService } from '../../shared/services/user.service';
import { PassengerRideHistoryResponseDTO } from '../../shared/models/passenger-ride-history.model';
import { ChangeDetectorRef } from '@angular/core';
import { RideService } from '../../shared/services/ride.service';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-passenger-rides-history',
  imports: [CommonModule, ReactiveFormsModule],
  standalone : true,
  templateUrl: './passenger-rides-history.component.html',
  styleUrl: './passenger-rides-history.component.css',
})
export class PassengerRidesHistoryComponent implements OnInit{
  constructor(private router:Router, private userService :UserService, private rideService : RideService, private cdr: ChangeDetectorRef){}
  //changeDetectorRef -manual control over angular change detection cycle 

  filterForm = new FormGroup({
    filterFrom: new FormControl<string | null>(null),
    filterTo: new FormControl<string | null>(null),
  });

  public rides: PassengerRideHistoryResponseDTO[] = [];
  public sortBy : string = "createdAt"; //default sorting
  public sortDirection: string = "DESC";

  ngOnInit() {
    this.loadRides();
  }
  search(){
    this.loadRides();
  }

  reset(){
    this.filterForm.reset();
    this.sortBy = "createdAt";
    this.sortDirection = "DESC";
    this.loadRides();
  }
  private buildIsoFromForm(): { fromIso?: string; toIso?: string } { //transform to spring boot format
    const from = this.filterForm.value.filterFrom; // YYYY-MM-DD
    const to = this.filterForm.value.filterTo;
    // spring expects ISO DATE_TIME LocalDateTime
    const fromIso = from ? `${from}T00:00:00` : undefined;
    const toIso = to ? `${to}T23:59:59` : undefined;
    return { fromIso, toIso };
  }

  getPickup(ride : PassengerRideHistoryResponseDTO){
    return ride.rideStops[0].address;
  }
  getDestination(ride : PassengerRideHistoryResponseDTO){
    return ride.rideStops[ride.rideStops.length - 1].address;
  }
  getAdditionalStops(ride: PassengerRideHistoryResponseDTO) : string[]{
    if(!ride.rideStops || ride.rideStops.length <= 2)
      return [];

    let additionalStops : string[] = [];
    for(let i = 1; i < ride.rideStops.length - 1; i ++){
        additionalStops.push(ride.rideStops[i].address);
    }
    return additionalStops;
  }
  loadRides(): void {
    const { fromIso, toIso } = this.buildIsoFromForm();
    this.userService.getRidesHistory({
      sortBy: this.sortBy,
      sortDirection: this.sortDirection,
      from: fromIso,
      to: toIso,
    }).subscribe({
      next: (response) => {this.rides = [...response]; // new reference (because of rendering problem)
                      this.cdr.detectChanges();   // force render now
        },
      error: (err) => console.error(err),
    });
  }

  sortPickupAddress(){
    this.sortBy = "pickupAddress";
    this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    this.loadRides();
  }
  sortDestinationAddress(){
    this.sortBy = "destinationAddress";
    this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    this.loadRides();
  }
  sortStartedAt(){
    this.sortBy = "startedAt";
    this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    this.loadRides();
  }
  sortFinishedAt(){
    this.sortBy = "finishedAt";
    this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    this.loadRides();
  }
  sortCreatedAt(){
    this.sortBy = "createdAt";
    this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    this.loadRides();
  }

  showMore(rideId : number){
    this.router.navigate(['ride-details', rideId]);
  }

}
