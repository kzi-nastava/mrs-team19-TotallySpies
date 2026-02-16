import { ReviewRequestDTO } from '../models/ride.model';

export const mockReview1: ReviewRequestDTO = {
  rating: 5,
  comment: 'all good!!'
};

export const mockReviewInvalid: ReviewRequestDTO = {
  rating: 0,
  comment: ''
};