import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { PanicNotificationDTO } from '../../shared/models/panic-notification.model';
import { AdminService } from '../../shared/services/admin.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-admin-notifications',
  imports: [CommonModule],
  templateUrl: './admin-notifications.component.html',
  styleUrl: './admin-notifications.component.css',
})
export class AdminNotificationsComponent implements OnInit{
  constructor(private adminService : AdminService,private cdr: ChangeDetectorRef){}

  public notifications : PanicNotificationDTO[] = []

  ngOnInit() {
    this.loadNotifications();
  }
  loadNotifications(){
    this.adminService.getNotifications().subscribe({
      next: (response) => {this.notifications = [...response]; // new reference (because of rendering problem)
                      this.cdr.detectChanges();   // force render now
        },
      error: (err) => console.error(err),
    });
  }
  

}
