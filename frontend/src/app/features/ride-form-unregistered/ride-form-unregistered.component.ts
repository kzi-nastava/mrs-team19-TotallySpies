import { Component } from '@angular/core';
import { MapComponent } from '../../shared/components/map/map.component';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-ride-form-unregistered',
  imports: [MapComponent, FormsModule, CommonModule],
  templateUrl: './ride-form-unregistered.component.html',
  styleUrl: './ride-form-unregistered.component.css',
})
export class RideFormUnregisteredComponent {
  pickup = '';
  destination = '';
  showError = false;

  constructor(private router: Router) {}

  search() {
    this.showError = false;
    if (!this.pickup.trim() || !this.destination.trim()) {
      this.showError = true;
      return;
    }

    this.router.navigate(['estimated-route'], {
      queryParams: {
        pickup: this.pickup,
        destination: this.destination,
      },
    });
  }
}
