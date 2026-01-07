import { DriverMarkerLocation } from '../models/driverMarkerLocation';
import { Coordinates } from '../models/coordinates';

export const DRIVERS_MOCK: DriverMarkerLocation[] = [
  {
    id: 1,
    location: { latitude: 45.2517, longitude: 19.8369 } as Coordinates,
    status: 'available',
  },
  {
    id: 2,
    location: { latitude: 45.2671, longitude: 19.8335 } as Coordinates,
    status: 'busy'
  },
  {
    id: 3,
    location: { latitude: 45.2400, longitude: 19.8200 } as Coordinates,
    status: 'reserved'
  },
];