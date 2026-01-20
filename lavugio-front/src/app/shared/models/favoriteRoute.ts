import { TripDestination } from "./tripDestination";

export interface FavoriteRoute {
  id: number;
  name: string;
  destinations: TripDestination[];
}