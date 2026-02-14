import { ChangeDetectorRef, Component, NgZone, OnInit } from '@angular/core';
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
import { DriverSearchModalComponent, DriverSearchStatus } from '../../shared/components/driver-search-modal/driver-search-modal.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
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
  driverSearchStatus: DriverSearchStatus = 'IDLE';
  driverErrorMessage: string | null = null;
  private detailedPath: any[] = [];
  stopsForMap: string[] = [];

  constructor(private fb: FormBuilder, private mapService: MapService,
    private rideService: RideService, private favouriteService: FavouriteService, 
    private cd: ChangeDetectorRef, private snackBar: MatSnackBar,
    private router: Router, private ngZone: NgZone) {
    this.routeForm = this.fb.group({
      start: ['', Validators.required],
      stops: this.fb.array([]),
      end: ['', Validators.required],
      emails: this.fb.array([]),
      vehicleType: ['standard'],
      babyTransport: [false],
      petTransport: [false],
      isScheduled: [false],
      scheduledTime: ['']
    });
  }

  showResults = false;

  onRouteCoordinates(coords: any[]) {
    console.log("path deliverd, num of points:", coords.length);
    this.detailedPath = coords;
  }

  ngOnInit() {
    // automatski reaguje cijena na promjenu vrste vozila
    this.routeForm.get('vehicleType')?.valueChanges.subscribe(() => {
      this.calculatePrice();
    });

    const nav = this.router.getCurrentNavigation();
    const state = nav && nav.extras && nav.extras.state ? nav.extras.state : history.state;

    const stops = state && state.stops ? state.stops : null;
    if (stops && Array.isArray(stops) && stops.length >= 2) {
      this.prefillFromStops(stops);
    }
  }
  //prefills ride stops from passenger history page
  private prefillFromStops(stops: any[]) {
    // reset i ocisti dynamic stops
    this.routeForm.reset({ vehicleType: 'standard', babyTransport: false, petTransport: false });
    this.stops.clear();

    // pickup
    this.routeForm.get('start')?.setValue(stops[0].address);

    // middle stops
    for (let i = 1; i < stops.length - 1; i++) {
      this.stops.push(this.fb.control(stops[i].address, Validators.required));
    }

    // destination stop
    this.routeForm.get('end')?.setValue(stops[stops.length - 1].address);

    // trigger map draw
    this.pickupForMap = stops[0].address;
    this.destinationForMap = stops[stops.length - 1].address;
    this.showResults = true;

    this.cd.detectChanges();
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

    const scheduledFor = this.validateAndGetScheduledTime();
    if (scheduledFor === "INVALID") {
      this.driverSearchStatus = 'IDLE';
      this.cd.detectChanges();
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
          petTransport: this.routeForm.value.petTransport,
          path: this.detailedPath,
          scheduledFor: scheduledFor
        };

        this.rideService.createRide(dto).subscribe({
          next: (res: any) => {

            if (res?.message?.toLowerCase().includes('scheduled')) {
              this.driverSearchStatus = 'SCHEDULED';
              this.driverErrorMessage = res.message;
            } else {
              this.driverSearchStatus = 'FOUND';
              this.driverErrorMessage = res.message;
            }

            this.cd.detectChanges();
          },
          error: (err: HttpErrorResponse) => {

            const errorMessage = err.error?.message || "";
            if (errorMessage.toLowerCase().includes('blocked')) {
              this.driverSearchStatus = 'BLOCKED';
              this.driverErrorMessage = errorMessage;
            } else {
              this.driverSearchStatus = 'NOT_FOUND';
              if (err.status === 400 && err.error?.message) {
                this.driverErrorMessage = err.error.message;
              } else if (err.status === 404) {
                this.driverErrorMessage = "Resource not found.";
              } else {
                this.driverErrorMessage = "Unexpected error occurred.";
              }
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

    const stopsArray = this.routeForm.get('stops') as FormArray;
    this.stopsForMap = stopsArray.value
      .map((s: string) => s.trim())
      .filter((s: string) => s.length > 0);

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
        this.cd.detectChanges();
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
handleModalClose() {
  console.log('Status pre zatvaranja:', this.driverSearchStatus);
  
  if (this.driverSearchStatus === 'FOUND' || this.driverSearchStatus === 'SCHEDULED') {
    this.driverSearchStatus = 'IDLE';
    this.cd.detectChanges();

    // Pokreni navigaciju unutar zone
    this.ngZone.run(() => {
      this.router.navigate(['/upcoming-rides']).then(success => {
        if (success) {
          console.log('Navigacija uspešna!');
        } else {
          console.error('Navigacija nije uspela. Proveri putanju u app.routes.ts');
        }
      });
    });
  } else {
    this.driverSearchStatus = 'IDLE';
  }
}

  validateAndGetScheduledTime(): string | null {
    if (!this.routeForm.value.isScheduled) return null;

    const timeValue = this.routeForm.get('scheduledTime')?.value;
    if (!timeValue) return null;

    const [hours, minutes] = timeValue.split(':').map(Number);
    const now = new Date();
    const scheduledDate = new Date();
    scheduledDate.setHours(hours, minutes, 0, 0);

    if (scheduledDate < now) {
      scheduledDate.setDate(scheduledDate.getDate() + 1);
    }

    const diffInMs = scheduledDate.getTime() - now.getTime();
    const diffInHours = diffInMs / (1000 * 60 * 60);

    if (diffInHours > 5) {
      this.showSnackBar("You can schedule a ride up to 5 hours in the future.", "error");
      return "INVALID";
    }

    const pad = (n: number) => n.toString().padStart(2, '0');

  return `${scheduledDate.getFullYear()}-${pad(scheduledDate.getMonth() + 1)}-${pad(scheduledDate.getDate())}T${pad(scheduledDate.getHours())}:${pad(scheduledDate.getMinutes())}:00`;
  }
  private showSnackBar(message: string, type: 'success' | 'error') {

    this.snackBar.open(message, 'OK', {
      duration: 4000,
      panelClass: type === 'success' ? ['confirm-snackbar'] : ['error-snackbar'],
      horizontalPosition: 'center',
      verticalPosition: 'top'

    });
  }

  onTimeInput(event: any, type: 'h' | 'm') {
    let val = event.target.value;
    if (val === '') return;

    val = Number(val);
    if (type === 'h') val = Math.min(23, Math.max(0, val));
    if (type === 'm') val = Math.min(59, Math.max(0, val));

    const current = this.routeForm.get('scheduledTime')?.value || '00:00';
    let [h, m] = current.split(':');

    if (type === 'h') h = val.toString().padStart(2, '0');
    if (type === 'm') m = val.toString().padStart(2, '0');

    this.routeForm.patchValue({
      scheduledTime: `${h}:${m}`
    });
  }
}