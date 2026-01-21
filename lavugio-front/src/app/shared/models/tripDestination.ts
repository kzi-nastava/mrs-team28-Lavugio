import { Coordinates } from "./coordinates";

export interface TripDestination {
  id: string;
  name: string;
  street: string;
  houseNumber: string;
  city: string;
  country: string;
  coordinates: Coordinates;
}