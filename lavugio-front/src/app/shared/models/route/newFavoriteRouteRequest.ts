import { TripDestination } from "../tripDestination";

export interface NewFavoriteRouteRequest {
    name: string;
    destinations: TripDestination[];
}