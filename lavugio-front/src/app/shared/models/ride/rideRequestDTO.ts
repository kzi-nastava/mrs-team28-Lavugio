import { RideDestinationDTO } from './rideDestinationDTO';
import { VehicleType } from '../vehicleType';

export interface RideRequestDTO {
  destinations: RideDestinationDTO[];
  passengerEmails: string[];
  vehicleType: VehicleType;
  babyFriendly: boolean;
  petFriendly: boolean;
  scheduledTime: string; // ISO 8601 format for transmission
  scheduled: boolean;

  getStartAddress?(): RideDestinationDTO;
}
