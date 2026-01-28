import { AfterViewInit, Component, effect, inject, Injector, OnDestroy, signal, ViewChild } from '@angular/core';
import { Navbar } from "@app/shared/components/navbar/navbar";
import { BaseInfoPage } from "../base-info-page/base-info-page";
import { Ride } from "./scheduled-rides/ride/ride";
import { ScheduledRideDTO } from '@app/shared/models/ride/scheduledRide';
import { Coordinates } from '@app/shared/models/coordinates';
import { ScheduledRides } from "./scheduled-rides/scheduled-rides";
import { MapComponent } from '@app/shared/components/map/map';
import { RideService } from '@app/core/services/ride-service';
import { catchError, EMPTY, Subscription, timeout } from 'rxjs';
import { DriverService } from '@app/core/services/driver-service';
import { FinishRide } from '@app/shared/models/ride/finishRide';
import { AuthService } from '@app/core/services/auth-service';
import { LocationService } from '@app/core/services/location-service';
import { DialogService } from '@app/core/services/dialog-service';

@Component({
  selector: 'app-driver-scheduled-rides',
  imports: [ScheduledRides, MapComponent, Navbar],
  templateUrl: './driver-scheduled-rides.html',
  styleUrl: './driver-scheduled-rides.css',
})
export class DriverScheduledRides implements AfterViewInit, OnDestroy{
  private authService = inject(AuthService);
  private driverService = inject(DriverService);
  private rideService = inject(RideService);
  private locationService = inject(LocationService);
  private dialogService = inject(DialogService);
  private subscription : Subscription | null = null;
  private injector = inject(Injector);
  rides = signal<ScheduledRideDTO[] | null>(null);
  coordinates = signal<Coordinates[] | null>(null);
  @ViewChild("map") map! : MapComponent;
  hasActiveRide = signal<boolean>(false);


  ngAfterViewInit(): void {
    this.loadRides();

    effect(()=>{
      let coords = this.coordinates(); 
      if (coords && this.map) {
        this.map.setRoute(coords);
      }
    }, {injector : this.injector})

  }

  loadRides(){
    const userId = this.authService.getStoredUser()?.userId;
    if (!userId) return;
    
    this.subscription = this.driverService.getScheduledRides(userId).pipe(
      timeout(5000),
      catchError(err => {
        console.error('Error fetching ride overview:', err);
        this.rides.set(null);
        return EMPTY;
      })
    ).subscribe(rides => {
      console.log('Ride Overview fetched:', rides);
      this.sortRidesByStatusAndTime(rides);
      this.checkIfAnyActiveRides(rides);
      this.rides.set(rides);
    });
  }

  updateCoordinates(coordinates : Coordinates[] | null){
    this.coordinates.set(coordinates);
  }

  ngOnDestroy(){
    this.subscription?.unsubscribe();
  }

  checkIfAnyActiveRides(rides: ScheduledRideDTO[]){
    this.hasActiveRide.set(false);
    for (let ride of rides){
      if (ride.status == "ACTIVE"){
        console.log("Active ride found:", ride.rideId);
        this.hasActiveRide.set(true);
        break;
      }
    }
  }

  sortRidesByStatusAndTime(rides: ScheduledRideDTO[]){
    rides.sort((a, b) => {
      // ACTIVE rides come first
      if (a.status === 'ACTIVE' && b.status !== 'ACTIVE') return -1;
      if (a.status !== 'ACTIVE' && b.status === 'ACTIVE') return 1;
      
      // For same status, sort by scheduled time
      const dateA = new Date(a.scheduledTime).getTime();
      const dateB = new Date(b.scheduledTime).getTime();
      return dateA - dateB;
    });
  }

  handleRideAction(event: {action: string, rideId: number}){
    const {action, rideId} = event;
    const currentRides = this.rides();
    
    if (!currentRides) return;
    
    switch(action) {
      case 'START':
        this.startRide(rideId);
        this.updateRideStatus(rideId, 'ACTIVE');
        break;
      case 'PANIC':
        this.setPanicStatus(rideId);
        break;
      case 'FINISH':
        this.finishRide(rideId);
        this.removeRideFromList(rideId);
        break;
      case 'FINISH_EARLY':
        this.finishRideEarly(rideId);
        break;
      case 'DENY':
        this.denyRide(rideId);
        break;
    }
  }

  updateRideStatus(rideId: number, newStatus: 'ACTIVE' | 'SCHEDULED'){
    const currentRides = this.rides();
    if (!currentRides) return;

    const updatedRides = currentRides.map(ride => 
      ride.rideId === rideId ? {...ride, status: newStatus} : ride
    );
    
    this.checkIfAnyActiveRides(updatedRides);
    this.rides.set(updatedRides);
  }

  setPanicStatus(rideId: number){
    const currentRide = this.rides()?.find(ride => ride.rideId === rideId);
    if (!currentRide) return;

    // Show confirmation dialog
    this.dialogService.openConfirm(
      'Panic confirmation',
      'Are you sure you want to send a panic alert? This will notify the administrators and mark your vehicle as in danger.'
    ).subscribe((confirmed: boolean) => {
      if (!confirmed) return;
      
      this.executePanicAlert(rideId);
    });
  }

  private executePanicAlert(rideId: number): void {
    const user = this.authService.getStoredUser();

    // Get current location
    this.locationService.getLocation()
      .then(position => {
        const currentLocation = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        };
        
        // Build panic alert DTO with driver information
        const panicAlert = {
          rideId: rideId,
          passengerId: user?.userId || 0,
          passengerName: `Passenger(s) on Ride #${rideId}`,
          driverName: user?.name || 'Driver',
          location: currentLocation,
          vehicleType: 'Driver Vehicle',
          vehicleLicensePlate: 'Check Driver Profile',
          message: 'EMERGENCY: Driver triggered panic button during active ride',
          timestamp: new Date().toISOString()
        };

        // Send panic to backend
        this.rideService.triggerPanic(rideId, panicAlert).subscribe({
          next: (response) => {
            console.log('✅ Panic alert sent successfully for ride:', rideId);
            console.log('Backend response:', response);
            
            // Force reload immediately to refresh the ride list
            window.location.reload();
          },
          error: (err) => {
            console.error('❌ Failed to send panic alert:', err);
            console.error('Error status:', err.status);
            console.error('Error response:', err.error);
            console.error('Full error object:', err);
            alert('❌ Failed to send panic alert.\n\nError: ' + (err.error?.error || err.message || 'Unknown error') + '\n\nPlease try again or contact support directly.');
          }
        });
      })
      .catch(error => {
        console.error('Location error:', error);
        alert(
          '❌ Location Required\n\n' +
          'Unable to get your location. Panic alerts require location data.\n\n' +
          'Error: ' + (error.message || error) + '\n\n' +
          'To enable location:\n' +
          '1. Click the lock icon (🔒) in the address bar\n' +
          '2. Find "Location" permission\n' +
          '3. Change it to "Allow"\n' +
          '4. Refresh the page and try again'
        );
      });
  }

  removeRideFromList(rideId: number){
    const currentRides = this.rides();
    if (!currentRides) return;

    const updatedRides = currentRides.filter(ride => ride.rideId !== rideId);
    
    this.checkIfAnyActiveRides(updatedRides);
    this.rides.set(updatedRides);
  }

  startRide(rideId: number){
    this.rideService.postStartRide(rideId).subscribe({
      next: () => console.log("Ride started successfully:", rideId),
      error: () => console.log("Error starting ride")
    });
  }

  finishRide(rideId: number){
    let finish: FinishRide = {
      rideId: rideId,
      finalDestination: { latitude: 0.0, longitude: 0.0},
      finishedEarly: false,
      distance: 1,
    }
    this.rideService.postRideFinish(finish).subscribe({
      next: () => console.log("Ride finished successfully:", rideId),
      error: () => console.log("Error finishing")
    })
  }

  finishRideEarly(rideId: number) {
    // Show confirmation dialog
    this.dialogService.openConfirm(
      'End Ride Early',
      'Are you sure you want to end this ride early? The destination will be updated to your current location and the price will be recalculated based on the distance traveled.'
    ).subscribe((confirmed: boolean) => {
      if (!confirmed) return;
      
      this.executeFinishEarly(rideId);
    });
  }

  private executeFinishEarly(rideId: number): void {
    // Get current location
    this.locationService.getLocation()
      .then(position => {
        const currentLocation = {
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        };
        
        // Get the ride to calculate actual distance
        const currentRide = this.rides()?.find(ride => ride.rideId === rideId);
        if (!currentRide) {
          alert('❌ Could not find ride information');
          return;
        }
        
        // Calculate approximate distance traveled (you may want to use a more accurate method)
        // For now, we'll use the ride's stored distance
        const distanceTraveled = currentRide.distance || 1.0;
        
        const finishEarlyData: FinishRide = {
          rideId: rideId,
          finalDestination: currentLocation,
          finishedEarly: true,
          distance: distanceTraveled
        };
        
        // Send finish early request to backend
        this.rideService.postRideFinish(finishEarlyData).subscribe({
          next: () => {
            console.log('✅ Ride finished early successfully:', rideId);
            alert('✅ Ride ended successfully. The price has been recalculated based on the distance traveled.');
            
            // Remove from list and reload
            this.removeRideFromList(rideId);
            window.location.reload();
          },
          error: (err) => {
            console.error('❌ Failed to finish ride early:', err);
            alert('❌ Failed to end ride early.\n\nError: ' + (err.error?.error || err.message || 'Unknown error'));
          }
        });
      })
      .catch(error => {
        console.error('Location error:', error);
        alert(
          '❌ Location Required\n\n' +
          'Unable to get your location. Ending a ride early requires location data to update the destination.\n\n' +
          'Error: ' + (error.message || error) + '\n\n' +
          'To enable location:\n' +
          '1. Click the lock icon (🔒) in the address bar\n' +
          '2. Find "Location" permission\n' +
          '3. Change it to "Allow"\n' +
          '4. Refresh the page and try again'
        );
      });
  }

  denyRide(rideId: number) {
    // Show prompt dialog for cancellation reason
    this.dialogService.openPrompt(
      'Cancel Ride',
      'Please provide a reason for canceling this ride:',
      'e.g., Passenger not at pickup location, health emergency, etc.',
      true
    ).subscribe((reason: string | null) => {
      if (!reason) return; // User canceled
      
      this.executeDenyRide(rideId, reason);
    });
  }

  private executeDenyRide(rideId: number, reason: string): void {
    this.rideService.cancelRideByDriver(rideId, reason).subscribe({
      next: () => {
        console.log('✅ Ride canceled successfully:', rideId);
        alert('✅ Ride canceled successfully.');
        this.removeRideFromList(rideId);
      },
      error: (err: any) => {
        console.error('❌ Failed to cancel ride:', err);
        alert('❌ Failed to cancel ride.\n\nError: ' + (err.error?.error || err.message || 'Unknown error'));
      }
    });
  }
}