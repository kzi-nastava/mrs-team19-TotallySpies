import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NotificationDTO } from '../../models/notification.model';
import { NotificationService } from '../../services/notification.service';
import { SocketService } from '../../services/socket.service';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../core/auth/services/auth.service';

@Component({
  selector: 'app-notification',
  imports: [CommonModule],
  templateUrl: './notification.component.html',
  styleUrl: './notification.component.css',
})
export class NotificationComponent implements OnInit {
  notifications: NotificationDTO[] = [];
  unreadCount: number = 0;
  isOpen: boolean = false;  // is dropdown opened

  constructor(
    private notificationService: NotificationService,
    private socketService: SocketService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
  ) { }

  ngOnInit(): void {
    this.loadHistory();
    this.subscribeToLiveNotifications();
  }

  loadHistory() {
    this.notificationService.getNotifications().subscribe(data => {
      // newswst first
      this.notifications = data
        .filter(n => !n.read)
        .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
      this.updateUnreadCount();
      this.cdr.detectChanges();
    });
  }


  subscribeToLiveNotifications() {
    this.socketService.notificationSubject.subscribe((notif: NotificationDTO | null) => {
      if (notif) {
        this.notifications.unshift(notif);
        this.updateUnreadCount();

        const audio = new Audio('sound/notification.mp3');
        audio.play();
      }
    });
  }

  updateUnreadCount() {
    this.unreadCount = this.notifications.filter(n => !n.read).length;
  }

  toggleDropdown() {
    this.isOpen = !this.isOpen;
  }

  // on click redirection
  onNotificationClick(notif: NotificationDTO) {
    if (!notif.read) {
      this.notificationService.markAsRead(notif.id).subscribe({
        next: () => {
          notif.read = true;
          this.updateUnreadCount();
          this.cdr.detectChanges();
        },
        error: (err) => console.error("Could not mark as read", err)
      });
    }

    this.isOpen = false;

    if (notif.rideId) {
      if (notif.type === 'LINKED_TO_RIDE') {
        this.router.navigate(['ride-tracker-user', notif.rideId]);
      }
      else if (notif.type === 'RIDE_COMPLETED') {
        this.router.navigate(['ride-tracker-user', notif.rideId]);
      }
      else if (notif.type === 'NEW_RIDE') {
        const userRole = this.authService.getRole();
        console.log(userRole);
        if (userRole === 'ROLE_DRIVER') {
          this.router.navigate(['scheduled-rides']);
        } else {
          this.router.navigate(['upcoming-rides']);
        }
      }
      else if (notif.type === 'RIDE_REMINDER') {
        this.router.navigate(['upcoming-rides']);
      }
    }
  }
}
