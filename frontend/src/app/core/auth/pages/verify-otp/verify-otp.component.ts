import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormArray} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { VerifyOtpDTO } from '../../model/verify-otp.model';
import { AuthService } from '../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar'; 

@Component({
  selector: 'app-verify-otp',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './verify-otp.component.html',
  styleUrl: './verify-otp.component.css',
})
export class VerifyOtpComponent {
  email = '';

  constructor(private route : ActivatedRoute, private router : Router, private authService : AuthService, private snackBar : MatSnackBar){
    this.route.queryParams.subscribe((params) => {
      this.email = (params['email'] ?? '').toString();
    })
  }

  private extractErrorMessage(err: any): string {
    return (
      err?.error?.message ??
      (typeof err?.error === 'string' ? err.error : null) ??
      (err?.status === 404 ? 'Email not found' : null) ??
      (err?.status === 400 ? 'Invalid OTP' : null) ??
      (err?.status === 417 ? 'OTP has expired' : null)??
      'OTP invalid.'
    );
  }
  otp = new FormArray(
  Array.from({ length: 6 }, () =>
    new FormControl('', [Validators.required, Validators.pattern(/^\d$/)])
  )
);

get code(): string {
  return this.otp.value.join('');
}

verifyOtpForm = new FormGroup({
  otp: this.otp
});

verifyOtp() : void{
  if (this.otp.valid){
    const verifyOtpDTO : VerifyOtpDTO = {
      email : this.email,
      otp : Number(this.code)
    }
    this.authService.verifyOtp(verifyOtpDTO).subscribe({
        next: (response) => {
          console.log('SUCCESS:', response);
          this.snackBar.open('OTP verified!', 'OK', {
            duration : 1200,
            horizontalPosition: 'center',
            verticalPosition : 'top',
            panelClass : ['confirm-snackbar']
          });
          this.router.navigate(['change-password'],{
            queryParams: {
            email: this.email,
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
  else{
    console.log("nije validan otp");
  }

}
onInput(e: Event, i: number) {
  const input = e.target as HTMLInputElement;
  input.value = input.value.replace(/\D/g, ''); // digits only
  this.otp.at(i).setValue(input.value); //input but with non-digits removed
} 

// onPaste(e: ClipboardEvent) {
//   e.preventDefault();
//   const text = (e.clipboardData?.getData('text') ?? '').replace(/\D/g, '').slice(0, this.otp.length);

//   text.split('').forEach((ch, idx) => this.otp.at(idx).setValue(ch));

//   const last = Math.min(text.length, this.otp.length) - 1;
//   const inputs = document.querySelectorAll<HTMLInputElement>('.otp-box');
//   inputs[last]?.focus();
// }
}
