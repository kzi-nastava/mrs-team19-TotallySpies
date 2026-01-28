import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AdminService } from '../../shared/services/admin.service';
import { VehicleType, CreateDriverRequestDTO } from '../../shared/models/users/user.model';
import { CommonModule } from '@angular/common';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-driver-registration',
  standalone: true,  // <-- ovo je važno
  imports: [ReactiveFormsModule, RouterModule, CommonModule, MatSnackBarModule],
  templateUrl: './driver-registration.component.html',
  styleUrls: ['./driver-registration.component.css']
})
export class DriverRegistrationComponent {
  vehicleTypes: string[] = [];
  driverForm: FormGroup;

  constructor(private adminService: AdminService, private router: Router, private snackBar: MatSnackBar) {
    this.vehicleTypes = Object.values(VehicleType);
    this.driverForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      firstName: new FormControl('', [Validators.required]),
      lastName: new FormControl('', [Validators.required]),
      address: new FormControl('', [Validators.required]),
      phone: new FormControl('', [
        Validators.required,
        Validators.pattern(/^\+381 6\d \d{3} \d{3,4}$/)
      ]),

      model: new FormControl('', [Validators.required]),
      type: new FormControl('', [Validators.required]),
      licensePlate: new FormControl('', [Validators.required]),
      seats: new FormControl(4, [Validators.required, Validators.min(1)]),
      babyFriendly: new FormControl(false),
      petFriendly: new FormControl(false)
    });
  }

  addDriver() {
    if (this.driverForm.valid) {
      const dto: CreateDriverRequestDTO = this.driverForm.value;
      console.log(this.driverForm.value);
      this.adminService.createDriver(dto).subscribe({
        next: () => {
          //alert('Driver successfully created!');
          this.snackBar.open(
            'Driver successfully created!',
            'OK',
            {
              duration: 4000,
              panelClass: ['confirm-snackbar'],
              horizontalPosition: 'center',
              verticalPosition: 'top'
            }
          ).afterDismissed().subscribe(() => {
            this.router.navigate(['/profile']);
          });;
        },
        error: (err) => {
          if (err.status === 409) {
            //alert('Driver with this email already exists.');
            this.snackBar.open(
              'Driver with this email already exists.',
              'OK',
              {
                duration: 4000,
                panelClass: ['error-snackbar'],
                horizontalPosition: 'center',
                verticalPosition: 'top'
              }
            );
          } else {
            console.error(err);
            //alert('Error creating driver.');
            this.snackBar.open(
              'Error creating driver.',
              'OK',
              {
                duration: 4000,
                panelClass: ['error-snackbar'],
                horizontalPosition: 'center',
                verticalPosition: 'top'
              }
            );
          }
        }
      });
    } else {
      alert('Please fill in all required fields.');
    }
  }

  cancel() {
    this.router.navigate(['/profile']);
  }
}
