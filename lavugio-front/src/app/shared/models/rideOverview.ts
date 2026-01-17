import { Coordinates } from "./coordinates";

export interface RideOverviewModel{
    rideId: number;
    driverId: number | null;
    price: number | null;
    status: "SCHEDULED" | "ACTIVE" | "FINISHED" | "CANCELLED" | "DENIED";
    driverCoordinates: Coordinates | null;
    checkpoints: Coordinates[];
    driverName: string;
    startAddress: string;
    endAddress: string | null;
    departureTime: Date | null;
    arrivalTime: Date | null;
    reported: Boolean;
    reviewed: Boolean;
}