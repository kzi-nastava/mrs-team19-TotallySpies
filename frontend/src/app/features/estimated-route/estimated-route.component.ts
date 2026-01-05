import { Component, OnInit } from '@angular/core';
import { MapComponent, RouteInfo } from '../../shared/components/map/map.component';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-estimated-route',
  imports: [MapComponent],
  templateUrl: './estimated-route.component.html',
  styleUrl: './estimated-route.component.css',
})
export class EstimatedRouteComponent {
  pickup = '';
  destination = '';
  estimatedTime: number | null = null;
  distanceKm: number | null = null;

  constructor(private route: ActivatedRoute, private router: Router) {
    //reading query params which were sent from previous page
    this.route.queryParams.subscribe((params) => {
      this.pickup = (params['pickup'] ?? '').toString();
      this.destination = (params['destination'] ?? '').toString();
      console.log('Pickup : ', this.pickup, ', destination: ', this.destination);
    });
  }

  goLogin() {
    this.router.navigate(['login']);
  }
  onRouteInfo(info: RouteInfo) {
    //calculated params from map component
    this.estimatedTime = info.estimatedTime;
    this.distanceKm = info.distanceKm;
  }
}
