export interface DriverRegistration {
    email: string;
    password: string;
    name: string;
    lastName: string;
    phoneNumber: string;
    address: string;

    vehicleMake: string;
    vehicleModel: string;
    licenseNumber: string;
    licensePlate: string;
    vehicleColor: string;
    vehicleType: string;

    petFriendly: boolean;
    babyFriendly: boolean;
}
