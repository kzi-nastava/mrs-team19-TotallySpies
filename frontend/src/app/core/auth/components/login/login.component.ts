import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { UserLoginRequestDTO } from '../../model/user-login-request.model';
import { UserTokenStateDTO } from '../../model/user-token-state.model';
import { CommonModule } from '@angular/common';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SocketService } from '../../../../shared/services/socket.service';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent implements OnInit{

  returnUrl: string = '/display-info';

  constructor(private authService: AuthService, private router: Router, private route: ActivatedRoute, private snackBar : MatSnackBar, private socketService: SocketService) {}

  loginForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    password: new FormControl('', [Validators.required,Validators.minLength(8)]),
  });
  get email(){
    return this.loginForm.controls.email;
  }

  get password(){
    return this.loginForm.controls.password;
  }
  private extractErrorMessage(err: any): string {
    return (
      err?.error?.message ??
      (typeof err?.error === 'string' ? err.error : null) ??
      (err?.status === 401 ? 'Invalid email or password.' : null) ??
      (err?.status === 403 ? 'Account not activated. Check your email.' : null) ??
      'Login failed.'
    );
  }

  ngOnInit() {
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/display-info';
  }

  login(): void {

    //this.error = null; //reset field
    if (this.loginForm.invalid) { //request is not sent
      this.loginForm.markAllAsTouched(); // forces showing validation errors
      return;
    }
    if (this.loginForm.valid) {
      const userLoginRequestDTO: UserLoginRequestDTO = {
        //make the login dto, like login request dto on server side
        email: this.loginForm.value.email || '',
        password: this.loginForm.value.password || '',
      };
      this.authService.login(userLoginRequestDTO).subscribe({
        //sends post request to server
        next: (response: UserTokenStateDTO) => {
          localStorage.setItem('token', response.accessToken); //get token from response and store it in
          // browser storage, token will be used later for authenticated requests
          this.authService.setUser(); //notify app that new user is logged in
          this.socketService.initializeWebSocketConnection();
          // alert("Successfully logged in!")
          // this.router.navigate(['home']);
          this.snackBar.open('Login successful', 'OK', {
            duration : 1000,
            horizontalPosition: 'center',
            verticalPosition : 'top',
            panelClass : ['confirm-snackbar']
          });
          
          this.router.navigateByUrl(this.returnUrl);
        },
        error: (err) => {
          this.snackBar.open(this.extractErrorMessage(err), 'OK', {
            horizontalPosition : 'center',
            verticalPosition : 'top',
            panelClass : ['error-snackbar']
          })
        },
      });
    }
  }
  
}
