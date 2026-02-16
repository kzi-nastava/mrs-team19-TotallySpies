import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReviewDialogComponent } from './review-dialog.component';
import { ReviewService } from '../../services/review.service';
import { ReviewServiceMock } from '../../mocks/review.service.mock';
import { mockReview1 } from '../../mocks/review.mock';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormsModule } from '@angular/forms';
import { of } from 'rxjs';

describe('ReviewDialogComponent', () => {
  let component: ReviewDialogComponent;
  let fixture: ComponentFixture<ReviewDialogComponent>;
  let reviewService: ReviewService;
  let dialogRefSpy: jasmine.SpyObj<MatDialogRef<ReviewDialogComponent>>;

  beforeEach(async () => {
    dialogRefSpy = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      imports: [ReviewDialogComponent, FormsModule],
      providers: [
        { provide: ReviewService, useClass: ReviewServiceMock },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: { type: 'driver', rideId: 10 } }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ReviewDialogComponent);
    component = fixture.componentInstance;
    reviewService = TestBed.inject(ReviewService);
    fixture.detectChanges();
  });

  it('should not submit if rating is 0 or comment is empty', () => {
    spyOn(window, 'alert');
    spyOn(reviewService, 'createDriverReview');
    
    component.selectedRating = 0;
    component.reviewText = '';
    
    component.submit();

    expect(window.alert).toHaveBeenCalledWith('Rating and comment are required.');
    expect(reviewService.createDriverReview).not.toHaveBeenCalled();
  });

  it('should call createDriverReview when type is driver and form is valid', () => {
    spyOn(window, 'alert');
    spyOn(reviewService, 'createDriverReview').and.returnValue(of({}));
    
    component.selectedRating = mockReview1.rating;
    component.reviewText = mockReview1.comment;
    
    component.submit();

    expect(reviewService.createDriverReview).toHaveBeenCalledWith(10, {
      rating: mockReview1.rating,
      comment: mockReview1.comment
    });
    expect(window.alert).toHaveBeenCalledWith('Review sent successfully.');
    expect(dialogRefSpy.close).toHaveBeenCalledWith(true);
  });

  it('should update selectedRating when setRating is called', () => {
    component.setRating(4);
    expect(component.selectedRating).toBe(4);
  });
});