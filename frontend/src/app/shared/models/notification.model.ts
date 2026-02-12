export interface NotificationDTO {
  id: number;
  rideId?: number;
  message: string;
  type: 'LINKED_TO_RIDE' | 'RIDE_COMPLETED';
  read: boolean;
  createdAt: string;
}