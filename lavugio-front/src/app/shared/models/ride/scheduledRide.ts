import { Coordinates } from "../coordinates";

export interface ScheduledRideDTO {
    rideId : number;
    startAddress : string;
    endAddress : string;
    scheduledTime : Date;
    checkpoints : Coordinates[];
    price: number;
    status: "ACTIVE" | "SCHEDULED";
    panicked: boolean;
}