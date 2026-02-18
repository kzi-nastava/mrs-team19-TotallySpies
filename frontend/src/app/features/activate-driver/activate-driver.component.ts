import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/auth/services/auth.service';

interface DriverActivationRequestDTO {
  token: string;
  password: string;
  confirmPassword: string;
}

@Component({
  selector: 'app-activate-driver',
  standalone: true,
  imports: [ReactiveFormsModule, RouterModule, HttpClientModule, CommonModule],
  templateUrl: './activate-driver.component.html',
  styleUrls: ['./activate-driver.component.css'],
})
export class ActivateDriverComponent {
  activateForm: FormGroup;
  token: string = '';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {
    // Uzmi token iz query parametara
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
    });

    this.activateForm = new FormGroup({
      password: new FormControl('', [Validators.required, Validators.minLength(8)]),
      confirmPassword: new FormControl('', [Validators.required])
    }, { validators: this.passwordsMatchValidator });
  }

  passwordsMatchValidator(control: AbstractControl): ValidationErrors | null {
    const pass = control.get('password')?.value;
    const confirm = control.get('confirmPassword')?.value;

    if (pass && confirm && pass !== confirm) {
      return { passwordsMismatch: true };
    }
    return null;
  }

  activateAccount() {
    if (this.activateForm.valid) {
      const dto: DriverActivationRequestDTO = {
        token: this.token,
        password: this.activateForm.value.password,
        confirmPassword: this.activateForm.value.confirmPassword
      };

      this.authService.activateDriver(dto).subscribe({
        next: () => {
          alert('Account activated successfully!');
          this.router.navigate(['/login']); 
        },
        error: (err) => {
          console.error(err);
          if (err.status === 400) {
            const errorMessage = err.error.message || 'Validation failed. Check your password.';
            alert(errorMessage);
          } else {
            alert('The link may be invalid or expired.');
          }
        }
      });
    } else {
      if (this.activateForm.errors?.['passwordsMismatch']) {
        alert('Passwords do not match!');
      } else {
        alert('Password must be at least 8 characters.');
      }
    }
  }
}