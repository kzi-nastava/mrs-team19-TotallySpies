import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { MapComponent } from '../../shared/components/map/map.component';
import { RouteInfo } from '../../shared/components/map/map.component';
import { forkJoin, map } from 'rxjs';
import { CreateRideRequestDTO, FavouriteRideDTO, RideStopDTO, VehicleType } from '../../shared/models/ride.model';
import { MapService } from '../../shared/components/map/map.service';
import { RideService } from '../../shared/services/ride.service';
import { HttpErrorResponse } from '@angular/common/http';
import { FavouriteService } from '../../shared/services/favourite.service';
import { DriverSearchModalComponent } from '../../shared/components/driver-search-modal/driver-search-modal.component';

@Component({
  selector: 'app-ride-ordering',
  templateUrl: './ride-ordering.component.html',
  styleUrls: ['./ride-ordering.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, DragDropModule, MapComponent, DriverSearchModalComponent]
})
export class RideOrderingComponent implements OnInit {
  routeForm: FormGroup;
  estimatedTime: number | null = null;
  distanceKm: number | null = null;
  estimatedPrice: number | null = null;
  pickupForMap = '';
  destinationForMap = '';
  showFavouritesModal = false;
  favourites: FavouriteRideDTO[] = [];
  driverSearchStatus: 'IDLE' | 'SEARCHING' | 'FOUND' | 'NOT_FOUND' = 'IDLE';
  driverErrorMessage: string | null = null;

  constructor(private fb: FormBuilder, private mapService: MapService,
    private rideService: RideService, private favouriteService: FavouriteService, private cd: ChangeDetectorRef) {
    this.routeForm = this.fb.group({
      start: ['', Validators.required],
      stops: this.fb.array([]),
      end: ['', Validators.required],
      emails: this.fb.array([]),
      vehicleType: ['standard'],
      babyTransport: [false],
      petTransport: [false]
    });
  }

  showResults = false;

  ngOnInit() {
    // automatski reaguje cijena na promjenu vrste vozila
    this.routeForm.get('vehicleType')?.valueChanges.subscribe(() => {
      this.calculatePrice();
    });
  }

  // funkcija koja hvata podatke iz MapComponent-a
  onRouteInfo(info: RouteInfo) {
    this.distanceKm = info.distanceKm;
    this.estimatedTime = info.estimatedTime;
    this.calculatePrice();
  }

  calculatePrice() {
    if (!this.distanceKm) return;

    const priceConfig: { [key: string]: number } = {
      'standard': 300,
      'luxury': 800,
      'van': 500
    };

    const selectedType = this.routeForm.get('vehicleType')?.value || 'standard';
    const pricePerKm = 120;

    this.estimatedPrice = Math.round(priceConfig[selectedType] + (this.distanceKm * pricePerKm));
  }

  canConfirmRide(): boolean {
    return this.routeForm.valid && this.distanceKm != null && this.estimatedTime != null;
  }

  confirmRide() {
    if (this.routeForm.invalid) {
      alert("Please fill in all fields");
      return;
    }

    if (!this.canConfirmRide()) {
      alert("Please wait for the route to be calculated.");
      return;
    }

    this.driverSearchStatus = 'SEARCHING';
    this.driverErrorMessage = null;

    const allAddresses: string[] = [
      this.routeForm.get('start')?.value,
      ...(this.routeForm.get('stops') as FormArray).value,
      this.routeForm.get('end')?.value
    ].filter(addr => !!addr && addr.trim() !== ''); 

    const geocodeRequests = allAddresses.map(addr =>
      this.mapService.search(addr).pipe(
        map((results: any) => {
          // provjera da li je API vratio rezultat
          if (!Array.isArray(results) || results.length === 0) {
            throw new Error(`Location not found: ${addr}`);
          }
          return {
            address: addr,
            lat: Number(results[0].lat),
            lng: Number(results[0].lon)
          } as RideStopDTO;
        })
      )
    );

    const validEmails: string[] = this.emails.controls
      .map(control => (control.value ?? '').trim())
      .filter(email => email !== '');

    forkJoin(geocodeRequests).subscribe({
      next: (locations: RideStopDTO[]) => {
        const dto: CreateRideRequestDTO = {
          locations: locations,
          vehicleType: this.mapVehicleType(this.routeForm.value.vehicleType),
          distanceKm: this.distanceKm!,
          estimatedTime: this.estimatedTime!,
          passengerEmails: validEmails,
          babyTransport: this.routeForm.value.babyTransport,
          petTransport: this.routeForm.value.petTransport
        };

        this.rideService.createRide(dto).subscribe({
          next: () => {
            this.driverSearchStatus = 'FOUND';
            this.cd.detectChanges();
          },
          error: (err: HttpErrorResponse) => {
            this.driverSearchStatus = 'NOT_FOUND';
            if (err.status === 400 && err.error?.message) {
              this.driverErrorMessage = err.error.message; 
            } else if (err.status === 404) {
              this.driverErrorMessage = "Resource not found.";
            } else {
              this.driverErrorMessage = "Unexpected error occurred.";
            }
            this.cd.detectChanges();
          }
        });
      },
      error: (err: any) => {
        alert(err instanceof Error ? err.message : "Failed to geocode addresses.");
      }
    });
  }

  private mapVehicleType(type: string): any {
    switch (type.toLowerCase()) {
      case 'luxury':
        return 'LUXURIOUS';
      case 'van':
        return 'VAN';
      default:
        return 'STANDARD';
    }
  }

  onSearch() {
    if (this.routeForm.invalid) {
      alert("Please fill in start and destination.");
      return;
    }

    this.pickupForMap = this.routeForm.value.start;
    this.destinationForMap = this.routeForm.value.end;

    this.showResults = true;
  }

  cancelRide() {
    this.routeForm.reset({ vehicleType: 'standard' });
    this.pickupForMap = '';
    this.destinationForMap = '';
    this.showResults = false;
    this.estimatedPrice = null;
    this.distanceKm = null;
  }

  // Stops
  get stops(): FormArray<FormControl> {
    return this.routeForm.get('stops') as FormArray<FormControl>;
  }

  addStop() {
    this.stops.push(this.fb.control('', Validators.required));
  }

  removeStop(index: number) {
    this.stops.removeAt(index);
  }

  drop(event: CdkDragDrop<FormControl[]>) {
    const control = this.stops.at(event.previousIndex); // uzmi kontrolu sa stare pozicije
    this.stops.removeAt(event.previousIndex);          // ukloni je
    this.stops.insert(event.currentIndex, control);    // ubaci je na novu poziciju
  }

  // Emails
  get emails(): FormArray<FormControl> {
    return this.routeForm.get('emails') as FormArray<FormControl>;
  }

  addEmail() {
    this.emails.push(this.fb.control('', [Validators.required, Validators.email]));
  }

  removeEmail(index: number) {
    this.emails.removeAt(index);
  }

  submit() {
    if (this.routeForm.valid) {
      console.log(this.routeForm.value);
      alert(JSON.stringify(this.routeForm.value, null, 2));
    } else {
      alert('Please fill in all required fields.');
    }
  }

  openFavourites() {
    this.favouriteService.getFavourites().subscribe({
      next: (favs) => {
        this.favourites = favs;
        this.showFavouritesModal = true;
      }, error: (err) => {
        console.error('Favourite load error:', err);
        alert('Failed to load favourite rides');
      }
    });
  }

  selectFavourite(fav: FavouriteRideDTO) {
    const locations = fav.locations;

    if (!locations || locations.length < 2) {
      alert('Invalid favourite route');
      return;
    }

    // Reset
    this.routeForm.reset({ vehicleType: 'standard' });
    this.stops.clear();

    // START
    this.routeForm.get('start')?.setValue(locations[0].address);

    // STOPS
    for (let i = 1; i < locations.length - 1; i++) {
      this.stops.push(
        this.fb.control(locations[i].address, Validators.required)
      );
    }

    // END
    this.routeForm.get('end')?.setValue(
      locations[locations.length - 1].address
    );

    // Pošalji mapi
    this.pickupForMap = locations[0].address;
    this.destinationForMap = locations[locations.length - 1].address;
    this.showResults = true;

    this.showFavouritesModal = false;
  }
  closeDriverModal() {
    this.driverSearchStatus = 'IDLE';
    this.driverErrorMessage = null;

  }
}