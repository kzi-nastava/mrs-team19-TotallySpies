import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { LoginComponent } from './core/auth/pages/login/login.component';
import { RegisterComponent } from './core/auth/pages/register/register.component';
import { UserProfileComponent } from './features/user-profile/user-profile.component';
import { DriverProfileComponent } from './features/driver-profile/driver-profile.component';
import { AdminProfileComponent } from './features/admin-profile/admin-profile.component';
import { RideHistoryComponent } from './features/ride-history/ride-history.component';
import { MapComponent } from './shared/components/map/map.component';
import { RideFormUnregisteredComponent } from './features/ride-form-unregistered/ride-form-unregistered.component';
import { EstimatedRouteComponent } from './features/estimated-route/estimated-route.component';
import { DisplayInfoComponent } from './features/display-info/display-info.component';
import { DriverRegistrationComponent } from './features/driver-registration/driver-registration.component';
import { ActivateDriverComponent } from './features/activate-driver/activate-driver.component';
import { RideTrackerUserComponent } from './features/ride-tracker-user/ride-tracker-user.component';
import { RideOrderingComponent } from './features/ride-ordering/ride-ordering.component';
import { ScheduledRidesComponent } from './features/scheduled-rides/scheduled-rides.component';
import { ForgotPasswordComponent } from './core/auth/pages/forgot-password/forgot-password.component';
import { VerifyOtpComponent } from './core/auth/pages/verify-otp/verify-otp.component';
import { ChangePasswordComponent } from './core/auth/pages/change-password/change-password.component';
import { ActivateAccountComponent } from './core/auth/pages/activate-account/activate-account.component';
import { ChangeOldPasswordComponent } from './shared/components/change-old-password/change-old-password.component';

export const routes: Routes = [
  {
    path: 'home',
    component: HomeComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: 'forgot-password',
    component: ForgotPasswordComponent
  },
  {
    path: 'verify-otp',
    component : VerifyOtpComponent
  },
  {
    path: 'change-password',
    component : ChangePasswordComponent
  },
  {
    path : 'activate-account',
    component : ActivateAccountComponent
  },
  {
    path: 'profile',
    component: UserProfileComponent,
  },
  {
    path: 'driver-profile',
    component: DriverProfileComponent,
  },
  {
    path: 'admin-profile',
    component: AdminProfileComponent,
  },
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  {
    path: 'ride-history',
    component: RideHistoryComponent,
  },
  {
    path: 'map',
    component: MapComponent,
  },
  {
    path: 'ride-form-unregistered',
    component: RideFormUnregisteredComponent,
  },
  {
    path: 'estimated-route',
    component: EstimatedRouteComponent,
  },
  {
    path: 'display-info',
    component: DisplayInfoComponent,
  },
  {
    path: 'driver-registration',
    component: DriverRegistrationComponent
  },
  {
    path: 'activate-driver-account',
    component: ActivateDriverComponent
  },
  {
    path: 'ride-tracker-user',
    component: RideTrackerUserComponent
  },
  {
    path: 'ride-ordering',
    component: RideOrderingComponent
  },
  {
    path: 'scheduled-rides',
    component: ScheduledRidesComponent
  },
  { path: 'change-old-password', 
    component: ChangeOldPasswordComponent 
  }
];
