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
export interface RideDetailsStopDTO{
  address:string,
  latitude :number,
  longitude : number
}
export interface RideStopDTO {
  address: string,
  lat: number,
  lng: number,
  orderIndex: number
}

export interface CreateRideRequestDTO {
  locations: RideStopDTO[],
  vehicleType: VehicleType,
  passengerEmails: string[],
  distanceKm: number,
  estimatedTime: number,
  babyTransport: boolean,
  petTransport: boolean,
  path: any[],
  scheduledFor: string | null;
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
  rating: number;
}

export interface InconsistencyReportRequestDTO {
  description: string;
}

export interface RideDetailsDTO {

  rideId: number;
  status: 'ACTIVE' | 'SCHEDULED' | 'COMPLETED' | 'STOPPED';
  startLocation: string;
  endLocation: string;
  passengers?: Passenger[];
  nextRideId?: number | null;
  startTime: string;     
  endTime?: string;
  price: number;
  passengersEmails?: string[];
  cancelled?: boolean;
  cancelledBy?: string | null;
  cancellReason?: string | null;
  panicPressed?: boolean;
  panicReason?: string | null;
  driverName?: string;
  driverImage?: string;
  carModel?: string;
  driverRating?: number;
}

export interface DriverRideHistoryDTO {
  id: number;
  startTime: string;     
  endTime: string;
  price: number;
  passengers: string[];
  startLocation: string;
  endLocation: string;
  cancelled: boolean;
  cancelledBy: string | null;
  cancellReason: string | null;
  panicPressed: boolean;
  panicReason: string | null;
}

export interface ActiveRideDTO {
  id: number;
  startTime: string; 
  endTime: string;
  price: number;
  passengers: string[]; 
  startLocation: string;
  endLocation: string;
  driverId: number;
  driverName: string;
  driverEmail: string;
  driverPicture: string; 
  vehicleModel: string; 
  driverAverageRating: number;
  panicPressed: boolean;
  panicReason: string;
  status: string; 
}

export interface PassengerUpcomingRideDTO {
  rideId: number;
  status: 'PENDING' | 'SCHEDULED';
  price: number;

  driverName?: string;

  locations: RideStopDTO[];
}
