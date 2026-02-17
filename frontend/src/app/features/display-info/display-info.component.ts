import { Component } from '@angular/core';
import { MapComponent } from '../../shared/components/map/map.component';
import { Router } from '@angular/router';
import { AuthService } from '../../core/auth/services/auth.service';

@Component({
  selector: 'app-display-info',
  imports: [MapComponent],
  templateUrl: './display-info.component.html',
  styleUrl: './display-info.component.css',
})
export class DisplayInfoComponent {
  constructor(private router: Router, private authService : AuthService){}
  orderRide() {
  if (!this.authService.isLoggedIn()) {
    this.router.navigate(['/ride-form-unregistered']);
  }
}
  
}
