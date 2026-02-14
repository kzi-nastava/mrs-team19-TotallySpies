import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { RideService } from '../../shared/services/ride.service';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MapComponent } from '../../shared/components/map/map.component';
import { RideDetailsStopDTO } from '../../shared/models/ride.model';
import { PassengerRideDetailsResponseDTO } from '../../shared/models/passenger-ride-details.model';
import { RideGradeDTO } from '../../shared/models/ride-grade.model';
import { FavouriteService } from '../../shared/services/favourite.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-ride-details',
  imports: [CommonModule, MapComponent],
  templateUrl: './ride-details.component.html',
  styleUrl: './ride-details.component.css',
})
export class RideDetailsComponent implements OnInit {
  constructor(private router: Router, private rideService: RideService, private route: ActivatedRoute, private cdr: ChangeDetectorRef,
    private favService: FavouriteService, private snackBar: MatSnackBar
  ) { }
  rideStops: RideDetailsStopDTO[] = [];
  driverName: string = '';
  driverLastName: string = '';
  driverEmail: string = '';
  driverPhoneNumber: string = '';
  distanceKm: number = 0;
  totalPrice: number = 0;
  reportReasons: Record<string, string> = {}
  //rideGrades : Record<string,number> = {}
  rideGrades: RideGradeDTO[] = [];
  rideId!: number;
  isFavourite: boolean = false;


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
          this.rideId = Number(this.route.snapshot.paramMap.get('id'));
        this.loadFavourites();
        this.cdr.detectChanges();
        console.log(response);
      },
      error: (err) => console.error(err),
    });
  }
  bookRoute(): void {
    if (!this.rideStops || this.rideStops.length < 2) return;
    this.router.navigate(['ride-ordering'], {
      state: {
        stops: this.rideStops
      }
    });

  }

  loadFavourites() {
    this.favService.getFavourites().subscribe({
      next: favs => {
        this.isFavourite = favs.some(f => f.id === this.rideId);
        this.cdr.detectChanges();
      },
      error: err => console.error(err)
    });
  }

  toggleFavourite() {
    if (this.isFavourite) {
      this.favService.removeFromFavourites(this.rideId).subscribe({
        next: () => {
          this.isFavourite = false;
          this.snackBar.open(
            'Ride removed from favourites ❌',
            'OK',
            {
              duration: 4000,
              panelClass: ['confirm-snackbar'],
              horizontalPosition: 'center',
              verticalPosition: 'top'
            }
          );
        },
        error: () => {
          this.snackBar.open(
            'Error removing ride from favourites',
            'OK',
            {
              duration: 4000,
              panelClass: ['error-snackbar'],
              horizontalPosition: 'center',
              verticalPosition: 'top'
            }
          );
        }
      });
    } else {
      this.favService.addToFavourites(this.rideId).subscribe({
        next: () => {
          this.isFavourite = true;
          this.snackBar.open(
            'Ride added to favourites ⭐',
            'OK',
            {
              duration: 4000,
              panelClass: ['confirm-snackbar'],
              horizontalPosition: 'center',
              verticalPosition: 'top'
            }
          );
        },
        error: () => {
          this.snackBar.open(
            'Error adding ride to favourites',
            'OK',
            {
              duration: 4000,
              panelClass: ['error-snackbar'],
              horizontalPosition: 'center',
              verticalPosition: 'top'
            }
          );
        }
      });
    }
  }
}

