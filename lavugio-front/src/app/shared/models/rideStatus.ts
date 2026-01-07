export interface RideStatus{
    rideId: number;
    status: "pending" | "in_progress" | "completed" | "canceled";
}