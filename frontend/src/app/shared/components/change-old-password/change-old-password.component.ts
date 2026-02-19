import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router } from '@angular/router';
import { UserService } from '../../services/user.service';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

@Component({
  selector: 'app-change-old-password',
  imports: [CommonModule, ReactiveFormsModule, MatSnackBarModule],
  templateUrl: './change-old-password.component.html',
  styleUrl: './change-old-password.component.css',
})
export class ChangeOldPasswordComponent {
  changeOldPasswordForm: FormGroup;

  constructor(private userService: UserService, private router: Router, private snackBar: MatSnackBar) {
    this.changeOldPasswordForm = new FormGroup({
      password: new FormControl('', [Validators.required]),
      newPassword: new FormControl('', [Validators.required, Validators.minLength(8)]),
      confirmNewPassword: new FormControl('', [Validators.required])
    }, { validators: this.passwordMatchValidator }); // Custom validator za poklapanje lozinki
  }

  // Helperi za lakši pristup u HTML-u (getters)
  get password() { return this.changeOldPasswordForm.get('password')!; }
  get newPassword() { return this.changeOldPasswordForm.get('newPassword')!; }
  get repeatedPassword() { return this.changeOldPasswordForm.get('confirmNewPassword')!; }

  // Custom validator koji poredi lozinke
  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const newPass = control.get('newPassword');
    const confirmPass = control.get('confirmNewPassword');
    return newPass && confirmPass && newPass.value !== confirmPass.value ? { passwordMismatch: true } : null;
  }

  changePassword() {
    if (this.changeOldPasswordForm.valid) {
      const request = {
        currentPassword: this.password.value,
        newPassword: this.newPassword.value,
        confirmNewPassword: this.repeatedPassword.value
      };

      this.userService.changePassword(request).subscribe({
        next: () => {
          //alert('Password changed successfully!');
          this.snackBar.open(
            'Password changed successfully!',
            'OK',
            {
              duration: 3500,
              panelClass: ['confirm-snackbar'],
              horizontalPosition: 'center',
              verticalPosition: 'top'
            }
          ).afterDismissed().subscribe(() => {
            this.router.navigate(['/login']);
          });;
        },
        error: (err) => {
          let msg = 'Failed to change password.';

          if (err.status === 400 && err.error.errors) {
            msg = Object.values(err.error.errors).join(', ');
          }
          
          else if (err.error && err.error.message) {
            msg = err.error.message;
          }

          this.snackBar.open(msg, 'OK', {
            duration: 4000,
            panelClass: ['error-snackbar'],
            horizontalPosition: 'center',
            verticalPosition: 'top'
          });
        }
      });
    } else {
      this.changeOldPasswordForm.markAllAsTouched();
    }
  }
}