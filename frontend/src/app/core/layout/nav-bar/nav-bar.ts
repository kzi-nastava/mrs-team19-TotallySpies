import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../auth/services/auth.service';
import { SocketService } from '../../../shared/services/socket.service';
import { CommonModule } from '@angular/common';
import { NotificationComponent } from '../../../shared/components/notification/notification.component';

@Component({
  selector: 'app-nav-bar',
  imports: [RouterModule, CommonModule, NotificationComponent],
  templateUrl: './nav-bar.html',
  styleUrl: './nav-bar.css',
})
export class NavBar {
  constructor(private authService : AuthService, private router : Router, private socketService: SocketService){}
  logout() : void {
    this.socketService.disconnect();
    this.authService.logout();
    this.router.navigate(['login']);
  }

  get isPassenger(): boolean {
    return this.authService.getRole() === 'ROLE_PASSENGER';
  }
}
