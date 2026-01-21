import { Coordinates } from "./coordinates";

export interface RideOverviewUpdate{
    endAddress?: string;
    destinationCoordinates?: Coordinates;
    departureTime?: Date;
    arrivalTime?: Date;
    status?: "SCHEDULED" | "ACTIVE" | "FINISHED" | "CANCELLED" | "DENIED";
    price?: number;
}