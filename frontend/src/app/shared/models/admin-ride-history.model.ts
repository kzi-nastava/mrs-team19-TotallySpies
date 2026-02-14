import { RideDetailsStopDTO } from "./ride.model";

export interface AdminRideHistoryResponseDTO{
    rideId : number;
    rideStops : RideDetailsStopDTO[];
    startedAt : string;
    finishedAt : string;
    createdAt : string;
    totalPrice : number;
    userWhoCancelled : string;
    cancelled : boolean;
    panic : boolean;
}