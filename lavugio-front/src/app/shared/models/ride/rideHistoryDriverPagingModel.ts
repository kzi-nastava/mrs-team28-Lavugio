import {RideHistoryDriverModel} from './rideHistoryDriver'

export interface RideHistoryDriverPagingModel{
    driverHistory: RideHistoryDriverModel[];
    totalElements: number;
    reachedEnd: boolean;
}