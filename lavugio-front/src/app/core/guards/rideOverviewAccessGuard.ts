import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  Router,
} from '@angular/router';
import {AuthService} from '../services/auth-service';
import { RideService } from '../services/ride-service';


@Injectable({
  providedIn: 'root',
})
export class RideOverviewAccessGuard implements CanActivate {
  constructor(
    private router: Router,
    private rideService: RideService,
    private authService: AuthService
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
  ): boolean {
    const userRole : string | null = this.authService.getUserRole();
    const rideId = route.paramMap.get('rideId');
    if (!rideId){
      return false;
    }
    const canAccess = this.rideService.canAccess(+rideId);

    const allowedRoles = route.data?.['role'];

    if (userRole == null) {
      this.router.navigate(['home-page']);
      return false;
    }

    if (!allowedRoles.includes(userRole) || !canAccess) {
      return false;
    }
    return true;
  }
}
