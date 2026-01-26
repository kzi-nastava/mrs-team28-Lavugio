import { Coordinates } from "../coordinates";
import { PassengerModel } from "../user/passenger";

export interface RideHistoryDriverDetailedModel{
    start : Date;
    end : Date;
    departure : string;
    destination : string;
    price : number;
    cancelled : boolean;
    panic : boolean;
    passengers: PassengerModel[];
    checkpoints : Coordinates[];
}