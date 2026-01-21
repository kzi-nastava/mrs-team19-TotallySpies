import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ChangedPasswordDTO } from '../../model/changed-password.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-change-password',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css',
})
export class ChangePasswordComponent {
  error: string | null = null;
  email = ''
  constructor(private route : ActivatedRoute, private router : Router, private authService : AuthService, private snackBar : MatSnackBar){
    this.route.queryParams.subscribe((params) => {
      this.email = (params['email'] ?? '').toString();
    })
  }
  private extractErrorMessage(err: any): string {
    return (
      err?.error?.message ??
      (typeof err?.error === 'string' ? err.error : null) ??
      (err?.status === 417 ? 'Please enter the password again.' : null) ??
      'Login failed.'
    );
  }
  changePasswordForm = new FormGroup({
    password: new FormControl('', [Validators.required]),
    repeatedPassword: new FormControl('', Validators.required),
  });
  get password(){
    return this.changePasswordForm.controls.password;
  }

  get repeatedPassword(){
    return this.changePasswordForm.controls.repeatedPassword;
  }

  changePassword() : void{
    if (this.changePasswordForm.valid){
      const changedPasswordDTO : ChangedPasswordDTO = {
        password : this.changePasswordForm.value.password || '',
        repeatedPassword : this.changePasswordForm.value.repeatedPassword || '',
        email : this.email || ''
      }
      this.authService.changePassword(changedPasswordDTO).subscribe({
        next : (response) =>{
          console.log('SUCCESS', response);
          this.snackBar.open('Password has been changed!', 'OK', {
            duration : 1200,
            horizontalPosition: 'center',
            verticalPosition : 'top',
            panelClass : ['confirm-snackbar']
          });
          this.router.navigate(['login']);
        },
        error : (err) =>{
          console.log('ERROR:', err),
          this.snackBar.open(this.extractErrorMessage(err), 'OK', {
            horizontalPosition : 'center',
            verticalPosition : 'top',
            panelClass : ['error-snackbar']
          })
        }
      })
    }
  }
}
