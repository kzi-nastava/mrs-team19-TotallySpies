import { ListFormat } from "typescript";

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
export enum RideStatus {
    COMPLETED,
    CANCELLED,
    ACTIVE,
    SCHEDULED,
    STOPPED,
    PENDING,
    REJECTED
}

export enum VehicleType {
    STANDARD,
    LUXURIOUS,
    VAN
}

export interface CreateRideResponseDTO {
  rideId: number,
  status: RideStatus,
  driverEmail: string,
  driverName: string,
  distanceKm: number,
  estimatedTime: number
}

export interface RideStopDTO {
  address: string,
  lat: number,
  lng: number
}

export interface CreateRideRequestDTO {
  locations: RideStopDTO[],
  vehicleType: VehicleType,
  passengerEmails: string[],
  distanceKm: number,
  estimatedTime: number,
  babyTransport: boolean,
  petTransport: boolean
}

export interface FavouriteRideDTO {
  id: number;
  locations: RideStopDTO[];
}

export interface ReviewRequestDTO {
  rating: number;
  comment: string;
}

export interface RideTrackingDTO {
  driverName: string;
  carModel: string;
  profilePicture: string;
  rideId: number;
  vehicleLat: number;
  vehicleLng: number;
  eta: number;
  status: 'ACTIVE' | 'SCHEDULED' | 'COMPLETED' | 'STOPPED';
  pickupAddress: string;
  destinationAddress: string;
}

export interface InconsistencyReportRequestDTO {
  description: string;
}

export interface RideDetailsDTO {
  driverName: string;
  carModel: string;
  profilePicture: string;
  status: 'ACTIVE' | 'SCHEDULED' | 'COMPLETED' | 'STOPPED';
  pickupAddress: string;
  destinationAddress: string;
  startTime?: string;
  endTime?: string;
  price?: number;
}