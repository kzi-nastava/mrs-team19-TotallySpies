import { RideDetailsStopDTO} from "./ride.model";

export interface PassengerRideHistoryResponseDTO{
    rideId : number;
    rideStops : RideDetailsStopDTO[];
    startedAt : string; //ISO string
    finishedAt : string;
    createdAt : string;
}