import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ReportDialogComponent } from '../../report-dialog/report-dialog.component';

@Component({
  selector: 'app-driver-info',
  imports: [CommonModule, MatDialogModule],
  templateUrl: './driver-info.component.html',
  styleUrl: './driver-info.component.css',
  standalone: true,
})
export class DriverInfoComponent {
  @Input() driverName: string = 'Dimitrije Antic';
  @Input() carModel: string = 'Subaru XV';
  @Input() rating: number = 5;
  @Input() profileImageUrl: string = '/images/driver_placeholder.png';
  constructor(private dialog: MatDialog) {}

  openReport() {
    this.dialog.open(ReportDialogComponent, {
      width: '25rem',
      panelClass: 'custom-dialog-container',
      backdropClass: 'blur-backdrop'
    });
  }
}