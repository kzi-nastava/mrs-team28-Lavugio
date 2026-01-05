import { Driver } from '../models/driver';
import { Coordinates } from '../models/coordinates';

export const DRIVERS_MOCK: Driver[] = [
  {
    location: { latitude: 45.2517, longitude: 19.8369 } as Coordinates,
    status: 'available'
  },
  {
    location: { latitude: 45.2671, longitude: 19.8335 } as Coordinates,
    status: 'busy'
  },
  {
    location: { latitude: 45.2400, longitude: 19.8200 } as Coordinates,
    status: 'scheduled'
  },
];