export class UserProfile {
  name: string = '';
  surname: string = '';
  phoneNumber: string = '';
  email: string = '';
  address: string = '';
  profilePhotoPath: string = '';
  role: 'DRIVER' | 'REGULAR_USER' | 'ADMINISTRATOR' = 'REGULAR_USER';

  vehicleMake?: string;
  vehicleModel?: string;
  vehicelColor?: string;
  vehicleLicensePlate?: string;
  vehicleSeats?: number;
  vehiclePetFriendly?: boolean;
  vehicleBabyFriendly?: boolean;
  vehicleType?: string;
  activeTime?: string;

  getRoleString(): string {
    switch (this.role) {
      case "DRIVER":
        return "Driver";
      case "REGULAR_USER":
        return "Regular User";
      case "ADMINISTRATOR":
        return "Administrator";
      default:
        return "Unknown";
    }
  }
}

export function getRoleString(role: 'DRIVER' | 'REGULAR_USER' | 'ADMINISTRATOR'): string {
  switch (role) {
    case "DRIVER": return "Driver";
    case "REGULAR_USER": return "Regular User";
    case "ADMINISTRATOR": return "Administrator";
    default: return "Unknown";
  }
}
