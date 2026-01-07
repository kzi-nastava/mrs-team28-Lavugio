import { Coordinates } from "./coordinates";

export interface RideStatus{
    rideId: number;
    status: "scheduled" | "in_progress" | "finished" | "cancelled";
    driverCoordinates: Coordinates | null;
    startCoordinates: Coordinates;
    endCoordinates: Coordinates | null;
    driverName: string;
    startAddress: string;
    endAddress: string | null;
    departureTime: string;
}