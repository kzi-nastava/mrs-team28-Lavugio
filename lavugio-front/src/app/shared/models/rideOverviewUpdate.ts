import { Coordinates } from "./coordinates";

export interface RideOverviewUpdate{
    destination?: string;
    destinationCoordinates?: Coordinates;
    departureTime?: Date;
    arrivalTime?: Date;
    status?: "scheduled" | "active" | "finished" | "cancelled" | "denied";
    price?: number;
}