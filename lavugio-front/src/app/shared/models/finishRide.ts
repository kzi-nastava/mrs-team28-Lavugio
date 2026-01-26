import { Coordinates } from "./coordinates";

export interface FinishRide {
    rideId: number,
    finalDestination: Coordinates,
    finishedEarly: boolean,
    distance: number
}