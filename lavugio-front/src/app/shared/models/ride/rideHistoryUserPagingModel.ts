import { RideHistoryUserModel } from './rideHistoryUser';

export interface RideHistoryUserPagingModel {
    userHistory: RideHistoryUserModel[];
    totalElements: number;
    reachedEnd: boolean;
}
