import { AfterViewInit, Component, effect, inject, Injector, OnDestroy, signal, ViewChild } from '@angular/core';
import { Navbar } from "@app/shared/components/navbar/navbar";
import { BaseInfoPage } from "../base-info-page/base-info-page";
import { Ride } from "./scheduled-rides/ride/ride";
import { ScheduledRideDTO } from '@app/shared/models/scheduledRide';
import { Coordinates } from '@app/shared/models/coordinates';
import { ScheduledRides } from "./scheduled-rides/scheduled-rides";
import { MapComponent } from '@app/shared/components/map/map';
import { RideService } from '@app/core/services/ride-service';
import { catchError, EMPTY, Subscription, timeout } from 'rxjs';
import { DriverService } from '@app/core/services/driver-service';
import { FinishRide } from '@app/shared/models/finishRide';

@Component({
  selector: 'app-driver-scheduled-rides',
  imports: [BaseInfoPage, ScheduledRides, MapComponent, Navbar],
  templateUrl: './driver-scheduled-rides.html',
  styleUrl: './driver-scheduled-rides.css',
})
export class DriverScheduledRides implements AfterViewInit, OnDestroy{
  private driverService = inject(DriverService);
  private rideService = inject(RideService);
  private subscription : Subscription | null = null;
  private injector = inject(Injector);
  driverId = 1;
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
    this.subscription = this.driverService.getScheduledRides(this.driverId).pipe(
      timeout(5000),
      catchError(err => {
        console.error('Error fetching ride overview:', err);
        this.rides.set(null);
        return EMPTY;
      })
    ).subscribe(rides => {
      console.log('Ride Overview fetched:', rides);
      this.sortRidesByScheduledTime(rides);
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
        this.hasActiveRide.set(true);
        break;
      }
    }
  }

  sortRidesByScheduledTime(rides: ScheduledRideDTO[]){
    rides.sort((a, b) => {
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
}