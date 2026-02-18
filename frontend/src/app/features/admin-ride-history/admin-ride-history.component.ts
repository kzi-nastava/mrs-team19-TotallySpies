import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RideService } from '../../shared/services/ride.service';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AdminRideHistoryResponseDTO } from '../../shared/models/admin-ride-history.model';
import { AdminService } from '../../shared/services/admin.service';
import { UserDTO } from '../../shared/models/users/user.model';

@Component({
  selector: 'app-admin-ride-history',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './admin-ride-history.component.html',
  styleUrl: './admin-ride-history.component.css',
})
export class AdminRideHistoryComponent implements OnInit {
constructor(private router:Router, private adminService :AdminService, private rideService : RideService, private cdr: ChangeDetectorRef){}
  //changeDetectorRef -manual control over angular change detection cycle 

  filterForm = new FormGroup({
    filterFrom: new FormControl<string | null>(null),
    filterTo: new FormControl<string | null>(null),
  });

  public rides: AdminRideHistoryResponseDTO[] = [];
  public users : UserDTO[] = [];
  public sortBy : string = "createdAt"; //default sorting
  public sortDirection: string = "DESC";
  public userId : number = 0;
  public userIndicator : number = 1;


  ngOnInit() {
    this.loadUsers();
  }
  showHistory(userRole:string, userId:number){
    this.userId = userId;
    if(userRole === 'PASSENGER')
      this.userIndicator = 2;
    else
      this.userIndicator = 1;
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

  getPickup(ride : AdminRideHistoryResponseDTO){
    return ride.rideStops[0].address;
  }
  getDestination(ride : AdminRideHistoryResponseDTO){
    return ride.rideStops[ride.rideStops.length - 1].address;
  }
  getAdditionalStops(ride: AdminRideHistoryResponseDTO) : string[]{
    if(!ride.rideStops || ride.rideStops.length <= 2)
      return [];

    let additionalStops : string[] = [];
    for(let i = 1; i < ride.rideStops.length - 1; i ++){
        additionalStops.push(ride.rideStops[i].address);
    }
    return additionalStops;
  }
  loadUsers() : void{
    this.adminService.getAllUsers().subscribe({
      next: (response) => {this.users = [...response]; // new reference (because of rendering problem)
                      this.cdr.detectChanges();   // force render now
        },
      error: (err) => console.error(err),
    });
  }
  loadRides(): void {
    const { fromIso, toIso } = this.buildIsoFromForm();
    this.adminService.getRidesHistory({
      userId : this.userId,
      userIndicator :  this.userIndicator,
      sortBy: this.sortBy,
      sortDirection: this.sortDirection,
      from: fromIso,
      to: toIso,
    }).subscribe({
      next: (response) => {this.rides = [...response]; // new reference (because of rendering problem)
                      this.cdr.detectChanges();   // force render now
                      console.log(response);
        },
      error: (err) => console.error(err),
    });

  }

  sortPickupAddress(){
    this.sortBy = "pickupAddress";
    this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    this.loadRides();
  }
  sortTotalPrice(){
    this.sortBy = "totalPrice";
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
  sortUserWhoCancelled(){
    this.sortBy = "userWhoCancelled";
    this.sortDirection = this.sortDirection === 'ASC' ? 'DESC' : 'ASC';
    this.loadRides();
  }
  showMore(rideId : number){
    this.router.navigate(['ride-details-admin', rideId]);
  }

}

