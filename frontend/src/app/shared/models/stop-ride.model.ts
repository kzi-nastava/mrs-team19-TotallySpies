export interface RideStopDTO {
  address: string;
  lat: number;
  lng: number;
}

export interface StopRideDTO {
  rideId: number;
  newEndTime: string;     // ISO string
  newTotalPrice: number;
  newDestination: RideStopDTO;
}
