import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../../../env/environment';
import { UserLoginRequestDTO } from '../model/user-login-request.model';
import { UserTokenStateDTO } from '../model/user-token-state.model';
import { JwtHelperService } from '@auth0/angular-jwt'; //decodes jwt and checks expiration
import { VerifyEmailDTO } from '../model/verify-email.model';
import { VerifyOtpDTO } from '../model/verify-otp.model';
import { ChangedPasswordDTO } from '../model/changed-password.model';

@Injectable({  //angular creates one singleton instance of AuthService for whole app
  providedIn: 'root',
})
export class AuthService {
  private headers = new HttpHeaders({
    'Content-Type': 'application/json',
    skip: 'true',
  });

  user$ = new BehaviorSubject<string | null>(null); //user stores the current auth state(in this case role)
  //behaviorSubject emits the current value to subscribers, it always remembers latest value
  userState = this.user$.asObservable(); //navbar is subscribed on userState(public observable)
  //  TODO :different navbar for every role

  constructor(private http: HttpClient) {
    this.user$.next(this.getRole()); //reads role from localStorage token,
    //push it into the subject so navbar knows if user is already logged in after refresh
  }

  //this sends POST request to specific login endpoint with body {email, password}
  login(dto: UserLoginRequestDTO): Observable<UserTokenStateDTO> {
    return this.http.post<UserTokenStateDTO>(environment.apiHost + '/auth/login',
      dto, 
      {headers: this.headers,}
    );
  }

  logout() : void{ //removes jwt from local storage
    localStorage.removeItem('token');
    this.user$.next(null);
  }
  verifyEmail(dto: VerifyEmailDTO): Observable<string> {
    return this.http.post(
      environment.apiHost + '/forgot-password/verify-email',
      dto,
      { headers: this.headers, responseType: 'text' }
    );
  }

  verifyOtp(dto : VerifyOtpDTO) : Observable<string>{
    return this.http.post(
      environment.apiHost + '/forgot-password/verify-otp',
      dto,
      { headers : this.headers, responseType: 'text'}
    );
  }
  changePassword(dto : ChangedPasswordDTO) : Observable<string>{
    return this.http.post(
      environment.apiHost + '/forgot-password/change-password',
      dto,
      { headers : this.headers, responseType : 'text'}
    );
  }
  register(formData: FormData): Observable<string> {
  // do not set 'Content-Type' here
  const uploadHeaders = new HttpHeaders({
    skip: 'true',
  });

  return this.http.post(environment.apiHost + '/auth/register', formData, {
    headers: uploadHeaders,
    responseType: 'text',
  });
  }
  activateAccount(token : string){
    const headers = new HttpHeaders({
      skip : 'true',   //to tell intercept to not attach jwt
    });
    return this.http.get(environment.apiHost + '/auth/activate',{
      headers,
      params: { token },
      responseType: 'text' 
    });
  }

  getRole(): any { //checks if token exists in localStorage, decodes it and extracts role
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('token');
      const helper = new JwtHelperService(); 
      //return helper.decodeToken(accessToken).role[0].authority;
      return helper.decodeToken(accessToken).role; //gets role from jwt claims to use it for custom navbar
    }
    return null;
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('token') != null;
  }

  setUser(): void {
    this.user$.next(this.getRole());
  }
}