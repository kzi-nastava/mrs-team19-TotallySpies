export interface RideFinishResponseDTO {
  rideId: number;
  totalPrice: number;
  displayTime: string;
  status: 'ACTIVE' | 'SCHEDULED' | 'COMPLETED';
  startLocation: string;
  endLocation: string;
  passengers: Passenger[];
  nextRideId?: number | null;
}

export interface Passenger {
  name: string;
  lastName: string; 
  profilePicture: string | null;
  address: string | null;
  email: string | null;
  phoneNumber: string | null;
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