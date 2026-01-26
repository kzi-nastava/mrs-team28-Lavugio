export interface StopBaseDTO {
  orderIndex: number;
  latitude: number;
  longitude: number;
}

export interface RideDestinationDTO {
  location: StopBaseDTO;
  address: string;
  streetName: string;
  city: string;
  country: string;
  streetNumber: number;
  zipCode: number;
}
