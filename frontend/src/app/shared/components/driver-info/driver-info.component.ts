import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ReportDialogComponent } from '../report-dialog/report-dialog.component';
import { ReviewDialogComponent } from '../review-dialog/review-dialog.component';

@Component({
  selector: 'app-driver-info',
  imports: [CommonModule, MatDialogModule],
  templateUrl: './driver-info.component.html',
  styleUrl: './driver-info.component.css',
  standalone: true,
})
export class DriverInfoComponent {
  @Input() rideId: number = 0;
  @Input() driverName: string = '';
  @Input() carModel: string = '';
  @Input() rating: number = 0;
  @Input() profileImageUrl: string = '';
  @Input() isFinished: boolean = false;

  constructor(private dialog: MatDialog) {}

  openReport() {
    this.dialog.open(ReportDialogComponent, {
      width: '25rem',
      data: { rideId: this.rideId }
    });
  }

  openReview(type: 'driver' | 'vehicle'): void {
    this.dialog.open(ReviewDialogComponent, { 
      width: '25rem', 
      data: { type, rideId: this.rideId } 
    });
  }
}