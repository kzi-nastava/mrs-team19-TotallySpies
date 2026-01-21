export interface Passenger {
  name: string;
  imageUrl: string;
}

export interface Ride {
  date: string;
  startTime: string;
  endTime: string;
  price: number;
  startLocation: string;
  endLocation: string;
  status: 'Finished' | 'Cancelled' | 'Upcoming' | 'Active';
  cancelledBy?: 'Driver' | 'Passenger';
  passengers: Passenger[];
  panicPressed: boolean;
  isUpcoming: boolean;
}