import { PassengerInfoDTO } from "./passenger-info.model";
import { RideGradeDTO } from "./ride-grade.model";
import { RideDetailsStopDTO } from "./ride.model";

export interface AdminRideDetailsResponseDTO{
    rideId : number;
    rideStops : RideDetailsStopDTO[];
    distanceKm : number;
    totalPrice :number;
    driverName : string;
    driverLastName : string;
    driverEmail : string;
    driverPhoneNumber : string;
    reportReasons: Record<string, string>; //passenger email and report reason
    rideGrades : RideGradeDTO[];
    passengersInfo : PassengerInfoDTO[];
}