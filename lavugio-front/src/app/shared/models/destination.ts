export interface Destination {
  id: string;
  display_name: string;
  lat: string;
  lon: string;
  type: string;
  address?: {
    city?: string;
    country?: string;
    state?: string;
  };
}