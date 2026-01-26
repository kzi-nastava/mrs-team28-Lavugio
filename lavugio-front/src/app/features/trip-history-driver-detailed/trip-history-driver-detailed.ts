import { AfterViewInit, Component, inject, input, OnDestroy, OnInit, signal, ViewChild } from '@angular/core';
import { BaseInfoPage } from '@app/features/base-info-page/base-info-page';
import { Passengers } from './passengers/passengers';
import { TripInfo } from './trip-info/trip-info';
import { MapComponent } from "@app/shared/components/map/map";
import { RideHistoryDriverDetailedModel } from '@app/shared/models/ride/rideHistoryDriverDetailed';
import { catchError, EMPTY, Subscription, timeout } from 'rxjs';
import { DriverService } from '@app/core/services/driver-service';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-trip-history-driver-detailed',
  imports: [BaseInfoPage, Passengers, TripInfo, MapComponent],
  templateUrl: './trip-history-driver-detailed.html',
  styleUrl: './trip-history-driver-detailed.css',
})
export class RideHistoryDriverDetailed implements AfterViewInit, OnDestroy{

  rideIdStr : string | null = "";
  rideId!: number | null;
  ride = signal<RideHistoryDriverDetailedModel | null>(null);
  sub! : Subscription;
  driverService = inject(DriverService);
  activatedRoute = inject(ActivatedRoute);
  @ViewChild('map') map!: MapComponent;


  ngAfterViewInit(): void {
    this.rideIdStr = this.activatedRoute.snapshot.paramMap.get("rideId");
    if (this.rideIdStr){
      this.rideId = parseInt(this.rideIdStr);
    } else{
      console.error("Couldn't parse ride id");
      return;
    }
    this.fetchRide();
  }

  fetchRide(): void {
    const rideId = this.rideId;
    if (rideId == undefined) {
      console.error("RideId is undefined");
      return;
    };
    this.sub = this.driverService.getDriverRideHistoryDetailed(rideId).pipe(
      timeout(5000),
      catchError(err => {
        console.error('Error fetching ride overview:', err);
        this.ride.set(null);
        return EMPTY;
      })
    ).subscribe(ride => {
      this.ride.set(ride);
      this.map.setRoute(ride.checkpoints);
      console.log("Ride fetched: ", ride);
    });
  }

  ngOnDestroy(): void {
    this.sub.unsubscribe();
  }
}
