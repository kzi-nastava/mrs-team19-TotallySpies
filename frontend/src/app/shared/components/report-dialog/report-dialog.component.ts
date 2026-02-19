import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { RideService } from '../../services/ride.service';
import { InconsistencyReportRequestDTO } from '../../models/ride.model';

@Component({
  selector: 'app-report-dialog',
  imports: [FormsModule],
  templateUrl: './report-dialog.component.html',
  styleUrl: './report-dialog.component.css',
  standalone: true,
})
export class ReportDialogComponent {
  reportText: string = '';
  rideId: number;
constructor(
    private dialogRef: MatDialogRef<ReportDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { rideId: number },
    private rideService: RideService
  ) {
    this.rideId = data.rideId;
  }  
  close(): void {
    this.dialogRef.close();
  }

  submit(): void {
    if (!this.reportText.trim()) {
      alert('Report needs to have content.');
      return;
    }

    const dto: InconsistencyReportRequestDTO = {
      rideId: this.rideId,
      description: this.reportText
    };

    this.rideService.reportInconsistency(this.rideId, dto).subscribe({
    next: () => {
      alert('Report sent successfully.');
      this.dialogRef.close(true);
    },
    error: (err: any) => {
      alert('Error sending report: ' + (err.error?.message || 'Server error'));
    }
  });
  }
}
