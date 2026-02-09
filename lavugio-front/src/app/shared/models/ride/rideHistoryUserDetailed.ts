import { Coordinates } from '../coordinates';

export interface ReportInfo {
    reportId: number;
    reportMessage: string;
    reporterName: string;
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

export interface RideHistoryUserDetailedModel {
    rideId: number;
    start: string;
    end: string;
    departure: string;
    destination: string;
    price: number;
    cancelled: boolean;
    panic: boolean;
    
    // Driver info
    driverId: number | null;
    driverName: string | null;
    driverLastName: string | null;
    driverPhotoPath: string | null;
    driverPhoneNumber: string | null;
    vehicleMake: string | null;
    vehicleModel: string | null;
    vehicleLicensePlate: string | null;
    vehicleColor: string | null;
    
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
