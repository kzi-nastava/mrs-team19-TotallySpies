import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { LoginComponent } from './core/auth/pages/login/login.component';
import { RegisterComponent } from './core/auth/pages/register/register.component';
import { UserProfileComponent } from './features/user-profile/user-profile.component';
import { DriverProfileComponent } from './features/driver-profile/driver-profile.component';
import { AdminProfileComponent } from './features/admin-profile/admin-profile.component';
import { RideHistoryComponent } from './features/ride-history/ride-history.component';
import { MapComponent } from './shared/components/map/map.component';

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
];
