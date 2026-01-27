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
    this.subscription = this.driverService.getScheduledRides().pipe(
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
      case 'DENY':
        this.removeRideFromList(rideId);
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
    const currentRides = this.rides();
    if (!currentRides) return;

    const updatedRides = currentRides.map(ride => 
      ride.rideId === rideId ? {...ride, panicked: true} : ride
    );
    
    this.rides.set(updatedRides);
    console.log('Panic set for ride:', rideId);
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
    this.rideService.putRideFinish(finish).subscribe({
      next: () => console.log("Ride finished successfully:", rideId),
      error: () => console.log("Error finishing")
    })
  }
}