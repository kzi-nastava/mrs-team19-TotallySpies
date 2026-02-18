import { Component, signal } from '@angular/core';
import { Router, NavigationEnd,RouterOutlet } from '@angular/router';
import { NavBar } from './core/layout/nav-bar/nav-bar';
import { filter } from 'rxjs/operators';
import 'leaflet-routing-machine';
import { CommonModule } from '@angular/common';
import { SupportChatComponent } from './features/support-chat/support-chat.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavBar, CommonModule, SupportChatComponent],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly title = signal('frontend');

  showNavBar = true;

  constructor(private router: Router) {
    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe((event: any) => {
        // Sakrij navbar na ovim stranicama
        const hiddenRoutes = ['/activate-driver-account'];
        this.showNavBar = !hiddenRoutes.some(route => event.url.includes(route));
      });
  }
}