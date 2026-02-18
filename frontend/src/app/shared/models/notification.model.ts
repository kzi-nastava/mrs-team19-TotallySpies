export interface NotificationDTO {
  id: number;
  rideId?: number;
  message: string;
  type: 'LINKED_TO_RIDE' | 'RIDE_COMPLETED' | 'NEW_RIDE' | 'RIDE_REJECTED' | 'RIDE_REMINDER';
  read: boolean;
  createdAt: string;
}