import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-block-dialog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './block-dialog.component.html',
  styleUrl: './block-dialog.component.css',
})
export class BlockDialogComponent {
  reason: string = '';

  constructor(
    public dialogRef: MatDialogRef<BlockDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { isBlocked: boolean; name: string }
  ) {}

  close(): void {
    this.dialogRef.close(null);
  }

  submit(): void {
    // Ako blokiramo, razlog je obavezan
    if (!this.data.isBlocked && !this.reason.trim()) {
      alert('Please enter a reason for blocking.');
      return;
    }

    this.dialogRef.close({
      confirm: true,
      reason: this.reason
    });
  }
}