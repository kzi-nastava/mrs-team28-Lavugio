import { Coordinates } from "./coordinates";

export interface DriverMarkerLocation{
    id: number;
    location: Coordinates;
    status: "available" | "busy" | "reserved";
}