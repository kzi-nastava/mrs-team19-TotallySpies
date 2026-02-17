export interface RideStopDTO {
  address: string;
  latitude: number;
  longitude: number;
}

export interface StopRideDTO {
  rideId: number;
  newEndTime: string;     // ISO string
  newTotalPrice: number;
  newDestination: RideStopDTO;
}
