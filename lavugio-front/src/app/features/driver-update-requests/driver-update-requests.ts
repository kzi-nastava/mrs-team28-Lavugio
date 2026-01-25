import { Component } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { DriverUpdateRequest } from './components/driver-update-request/driver-update-request';
import { UserProfile } from '@app/shared/models/user/userProfile';

interface DriverUpdateRequestData {
  oldProfile: UserProfile;
  newProfile: UserProfile;
  editRequestId: number;
}

@Component({
  selector: 'app-driver-update-requests',
  imports: [Navbar, DriverUpdateRequest],
  templateUrl: './driver-update-requests.html',
  styleUrl: './driver-update-requests.css',
})
export class DriverUpdateRequests {
  
  // Hardcoded test data for driver update requests
  updateRequests: DriverUpdateRequestData[] = [
    {
      editRequestId: 1,
      oldProfile: {
        name: 'John',
        surname: 'Smith',
        phoneNumber: '+1-555-0101',
        email: 'john.smith@example.com',
        address: '123 Main St, Springfield',
        profilePhotoPath: '/assets/profile1.jpg',
        role: 'DRIVER',
        vehicleMake: 'Toyota',
        vehicleModel: 'Camry',
        vehicleColor: 'Silver',
        vehicleLicensePlate: 'ABC-1234',
        vehicleSeats: 4,
        vehiclePetFriendly: true,
        vehicleBabyFriendly: false,
        vehicleType: 'Sedan',
        activeTime: '2024-01-15',
        getRoleString: () => 'Driver'
      },
      newProfile: {
        name: 'John',
        surname: 'Smith',
        phoneNumber: '+1-555-0101',
        email: 'john.smith@example.com',
        address: '456 Oak Avenue, Springfield',
        profilePhotoPath: '/assets/profile1.jpg',
        role: 'DRIVER',
        vehicleMake: 'Honda',
        vehicleModel: 'Accord',
        vehicleColor: 'Blue',
        vehicleLicensePlate: 'XYZ-5678',
        vehicleSeats: 5,
        vehiclePetFriendly: true,
        vehicleBabyFriendly: true,
        vehicleType: 'Sedan',
        activeTime: '2024-01-15',
        getRoleString: () => 'Driver'
      }
    },
    {
      editRequestId: 2,
      oldProfile: {
        name: 'Sarah',
        surname: 'Johnson',
        phoneNumber: '+1-555-0202',
        email: 'sarah.johnson@example.com',
        address: '789 Elm Street, Riverside',
        profilePhotoPath: '/assets/profile2.jpg',
        role: 'DRIVER',
        vehicleMake: 'Ford',
        vehicleModel: 'Explorer',
        vehicleColor: 'Black',
        vehicleLicensePlate: 'DEF-9012',
        vehicleSeats: 7,
        vehiclePetFriendly: false,
        vehicleBabyFriendly: true,
        vehicleType: 'SUV',
        activeTime: '2023-11-20',
        getRoleString: () => 'Driver'
      },
      newProfile: {
        name: 'Sarah',
        surname: 'Johnson',
        phoneNumber: '+1-555-0999',
        email: 'sarah.j.new@example.com',
        address: '789 Elm Street, Riverside',
        profilePhotoPath: '/assets/profile2.jpg',
        role: 'DRIVER',
        vehicleMake: 'Ford',
        vehicleModel: 'Explorer',
        vehicleColor: 'Black',
        vehicleLicensePlate: 'DEF-9012',
        vehicleSeats: 7,
        vehiclePetFriendly: true,
        vehicleBabyFriendly: true,
        vehicleType: 'SUV',
        activeTime: '2023-11-20',
        getRoleString: () => 'Driver'
      }
    },
    {
      editRequestId: 3,
      oldProfile: {
        name: 'Michael',
        surname: 'Brown',
        phoneNumber: '+1-555-0303',
        email: 'michael.brown@example.com',
        address: '321 Pine Road, Lakeside',
        profilePhotoPath: '/assets/profile3.jpg',
        role: 'DRIVER',
        vehicleMake: 'Tesla',
        vehicleModel: 'Model 3',
        vehicleColor: 'White',
        vehicleLicensePlate: 'GHI-3456',
        vehicleSeats: 5,
        vehiclePetFriendly: true,
        vehicleBabyFriendly: true,
        vehicleType: 'Electric',
        activeTime: '2024-02-01',
        getRoleString: () => 'Driver'
      },
      newProfile: {
        name: 'Michael',
        surname: 'Brown',
        phoneNumber: '+1-555-0303',
        email: 'michael.brown@example.com',
        address: '999 Technology Boulevard, Silicon Valley',
        profilePhotoPath: '/assets/profile3.jpg',
        role: 'DRIVER',
        vehicleMake: 'Tesla',
        vehicleModel: 'Model S',
        vehicleColor: 'Red',
        vehicleLicensePlate: 'TECH-2024',
        vehicleSeats: 5,
        vehiclePetFriendly: true,
        vehicleBabyFriendly: true,
        vehicleType: 'Electric',
        activeTime: '2024-02-01',
        getRoleString: () => 'Driver'
      }
    }
  ];
}
