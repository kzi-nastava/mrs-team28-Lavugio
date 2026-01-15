import { Coordinates } from "./coordinates";

export interface RideOverviewModel{
    rideId: number;
    driverId: number | null;
    price: number | null;
    status: "scheduled" | "active" | "finished" | "cancelled" | "denied";
    driverCoordinates: Coordinates | null;
    checkpoints: Coordinates[];
    driverName: string;
    startAddress: string;
    endAddress: string | null;
    departureTime: Date | null;
    arrivalTime: Date | null;
}