import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { ReviewRequestDTO } from '../../models/ride.model';
import { ReviewService } from '../../services/review.service';

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
  rideId: number = 1;

  constructor(
    public dialogRef: MatDialogRef<ReviewDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { type: string, rideId: number },
    private reviewService: ReviewService
  ) {
    this.rideId = data.rideId;
  }

  setRating(rating: number) {
    this.selectedRating = rating;
  }

  submit() {
    if (!this.reviewText.trim() || this.selectedRating === 0) {
      alert('Rating and comment are required.');
      return;
    }

    const dto: ReviewRequestDTO = {
      rating: this.selectedRating,
      comment: this.reviewText
    };

    const request = this.data.type === 'driver' 
      ? this.reviewService.createDriverReview(this.rideId, dto)
      : this.reviewService.createVehicleReview(this.rideId, dto);

    request.subscribe({
      next: () => {
        alert('Review sent successfully.');
        this.dialogRef.close(true);
      },
      error: (err: any) => alert('Error sending review: ' + err.error.message)
    });
  }

  close() { this.dialogRef.close(); }
}
