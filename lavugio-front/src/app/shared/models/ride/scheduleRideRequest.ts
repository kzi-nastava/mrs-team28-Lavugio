import { TripDestination } from "../tripDestination";

export interface ScheduleRideRequest {
    destinations: TripDestination[];
    vehicleType: string;
    isPetFriendly: boolean;
    isBabyFriendly: boolean;
    isScheduled: boolean;
    scheduledTime?: string; // ISO 8601 format
    passangers: string[];
    estimatedTimeMinutes: number;
    estimatedDistanceKm: number;
    price: number;
}