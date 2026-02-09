import { Coordinates } from "../coordinates";

export interface RideMonitoringModel{
    rideId: number,
    driverId: number,
    driverName: string,
    startTime: Date,
    startAddress: string,
    endAddress: string,
    checkpoints: Coordinates[];
}