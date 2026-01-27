import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-report-dialog',
  imports: [FormsModule],
  templateUrl: './report-dialog.component.html',
  styleUrl: './report-dialog.component.css',
  standalone: true,
})
export class ReportDialogComponent {
  reportText: string = '';
  constructor(private dialogRef: MatDialogRef<ReportDialogComponent>) {}
  
  close(): void {
    this.dialogRef.close();
  }

  submit(): void {
    if (this.reportText.trim()) {
      console.log('Report sent:', this.reportText);
      this.dialogRef.close();
      alert('Report sent successfully.');

    }
    else {
    alert('Report needs to have content.');
  }
}
}
