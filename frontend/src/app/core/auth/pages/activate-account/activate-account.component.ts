import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-activate-account',
  templateUrl: './activate-account.component.html',
  styleUrl: './activate-account.component.css',
  imports: [ReactiveFormsModule, CommonModule],
})
export class ActivateAccountComponent {
  status: 'loading' | 'success' | 'error' = 'loading';
  message = 'Activating your account...';

  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const token = (this.route.snapshot.queryParamMap.get('token') ?? '').trim(); //reading token from url

    if (!token) {
      this.status = 'error';
      this.message = 'Missing activation token.';
      return;
    }

    this.authService.activateAccount(token).subscribe({
      next: () => {
        this.status = 'success';
        this.message = 'Account activated successfully. You can now log in.';

        this.snackBar.open('Account activated!', 'OK', {
          duration: 2000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['confirm-snackbar'],
        });
        setTimeout(() => this.router.navigate(['login']), 1000);
      },
      error: (err) => {
        this.status = 'error';
        const msg =
          err?.error?.message ??
          (typeof err?.error === 'string' ? err.error : null) ??
          'Activation failed.';

        this.message = msg;

        this.snackBar.open(msg, 'OK', {
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['error-snackbar'],
        });
      },
    });
  }

  goToLogin() {
    this.router.navigate(['login']);
  }
}
