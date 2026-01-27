import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-review-dialog',
  imports: [CommonModule, FormsModule, MatDialogModule],
  templateUrl: './review-dialog.component.html',
  styleUrl: './review-dialog.component.css',
  standalone: true,
})
export class ReviewDialogComponent {
  reviewText: string = '';
  selectedRating: number = 0;

  constructor(
    public dialogRef: MatDialogRef<ReviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { type: string } // Prima 'driver' ili 'vehicle'
  ) {}

  setRating(rating: number) {
    this.selectedRating = rating;
  }

  close() { this.dialogRef.close(); }

  submit() {
    if (this.reviewText.trim()) {
      if (this.selectedRating === 0) {
        alert('Please select a star rating.');
        return;
      }
      console.log(`Review for ${this.data.type}:`, this.selectedRating, this.reviewText);
      this.dialogRef.close();
      alert('Review sent successfully.');
    } else {
      alert('Review needs to have content.');
    }
  }
}
