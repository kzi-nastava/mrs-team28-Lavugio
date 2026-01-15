import { TripDestination } from "./tripDestination";

export interface FavoriteRoute {
  id: string;
  name: string;
  destinations: TripDestination[];
}