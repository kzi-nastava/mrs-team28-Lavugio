import { UserProfile } from "./userProfile";

export interface EditProfileDTO {
    name: string;
    surname: string;
    phoneNumber: string;
    address: string;
}

export function MapProfileToEditProfileDTO(profile: UserProfile): EditProfileDTO {
    return {
        name: profile.name,
        surname: profile.surname,
        phoneNumber: profile.phoneNumber,
        address: profile.address,
    };
}

export interface EditDriverProfileRequestDTO {
    profile: EditProfileDTO;
    
    vehicleMake: string;
    vehicleModel: string;
    vehicleColor: string;
    vehicleLicensePlate: string;
    vehicleSeats: number;
    vehiclePetFriendly: boolean;
    vehicleBabyFriendly: boolean;
    vehicleType: string;
}

export function MapProfileToEditDriverProfileRequestDTO(profile: UserProfile): EditDriverProfileRequestDTO {
    const mapVehicleType = (type?: string): string => {
        if (!type) return '';

        const upper = type.toUpperCase();
        if (upper === 'STANDARD' || upper === 'LUXURY' || upper === 'COMBI') {
            return upper;
        }

        switch (type) {
            case 'Standard':
                return 'STANDARD';
            case 'Luxury':
                return 'LUXURY';
            case 'Combi':
                return 'COMBI';
            default:
                return upper;
        }
    };

    return {
        profile: MapProfileToEditProfileDTO(profile),
        vehicleMake: profile.vehicleMake || '',
        vehicleModel: profile.vehicleModel || '',
        vehicleColor: profile.vehicleColor || '',
        vehicleLicensePlate: profile.vehicleLicensePlate || '',
        vehicleSeats: profile.vehicleSeats || 0,
        vehiclePetFriendly: profile.vehiclePetFriendly || false,
        vehicleBabyFriendly: profile.vehicleBabyFriendly || false,
        vehicleType: mapVehicleType(profile.vehicleType),
    }
}

export interface DriverUpdateRequestDiffDTO {
    requestId: number;
    oldData: EditDriverProfileRequestDTO;
    newData: EditDriverProfileRequestDTO;
    email: string;
}
