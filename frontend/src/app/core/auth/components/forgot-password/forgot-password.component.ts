import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { VerifyEmailDTO } from '../../model/verify-email.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-forgot-password',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent {
  constructor(private authService: AuthService, private router: Router, private snackBar : MatSnackBar){};

  forgotPasswordForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
  });
  get email(){
    return this.forgotPasswordForm.controls.email;
  }
  private extractErrorMessage(err: any): string {
    return (
      err?.error?.message ??
      (typeof err?.error === 'string' ? err.error : null) ??
      (err?.status === 404 ? 'Email not found' : null) ??
      'Login failed.'
    );
  }
  verifyEmail(): void {
    if (this.forgotPasswordForm.invalid) { 
      this.forgotPasswordForm.markAllAsTouched(); 
      return;
    }
    if (this.forgotPasswordForm.valid) {
      const email : VerifyEmailDTO = {
        email : this.forgotPasswordForm.value.email || '',
      }
      this.authService.verifyEmail(email).subscribe({
        next: (response) => {
          console.log('SUCCESS:', response);
          const emailValue = (this.forgotPasswordForm.value.email ?? '').trim();
          this.snackBar.open('OTP code sent to your email!', 'OK', {
            duration : 1200,
            horizontalPosition: 'center',
            verticalPosition : 'top',
            panelClass : ['confirm-snackbar']
          });

          this.router.navigate(['verify-otp'],{
            queryParams: {
            email: emailValue
          },
        });
        },
        error: (err) => {
          console.log('ERROR:', err),
          this.snackBar.open(this.extractErrorMessage(err), 'OK', {
            horizontalPosition : 'center',
            verticalPosition : 'top',
            panelClass : ['error-snackbar']
          })
      }});
    }
  }
  
}
