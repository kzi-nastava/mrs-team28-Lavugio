import { Coordinates } from '../coordinates';

export interface RideHistoryAdminModel {
    rideId: number;
    startAddress: string;
    endAddress: string;
    startDate: string;
    endDate: string;
    price: number;
    cancelled: boolean;
    cancelledBy: string | null;
    panic: boolean;
}

export interface RideHistoryAdminPagingModel {
    adminHistory: RideHistoryAdminModel[];
    reachedEnd: boolean;
}

export interface ReportInfo {
    reportId: number;
    reportMessage: string;
    reporterName: string;
}

export interface PassengerInfo {
    passengerId: number;
    passengerName: string;
    passengerLastName: string;
    passengerEmail: string;
}

export interface DestinationDetail {
    orderIndex: number;
    latitude: number;
    longitude: number;
    address: string;
    streetName: string;
    city: string;
    country: string;
    streetNumber: string;
    zipCode: number;
}

export interface RideHistoryAdminDetailedModel {
    rideId: number;
    start: string;
    end: string;
    departure: string;
    destination: string;
    price: number;
    cancelled: boolean;
    cancelledBy: string | null;
    panic: boolean;
    
    // Driver info
    driverId: number | null;
    driverName: string | null;
    driverLastName: string | null;
    driverPhotoPath: string | null;
    driverPhoneNumber: string | null;
    driverEmail: string | null;
    vehicleMake: string | null;
    vehicleModel: string | null;
    vehicleLicensePlate: string | null;
    vehicleColor: string | null;
    
    // Passengers
    passengers: PassengerInfo[];
    
    // Review info
    driverRating: number | null;
    carRating: number | null;
    reviewComment: string | null;
    hasReview: boolean;
    
    // Reports
    reports: ReportInfo[];
    
    // Checkpoints for map
    checkpoints: Coordinates[];
    
    // Full destination info for reordering
    destinations: DestinationDetail[];
}
