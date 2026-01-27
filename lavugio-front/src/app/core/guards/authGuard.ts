import { Injectable } from '@angular/core';
import {
  CanActivate,
  ActivatedRouteSnapshot,
  Router,
} from '@angular/router';
import { AuthService } from '@app/core/services/auth-service';


@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private router: Router,
    private authService: AuthService
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
  ): boolean {
    const userRole : string | null = this.authService.getUserRole();

    const allowedRoles = route.data?.['role'];

    if (userRole == null) {
      this.router.navigate(['home-page']);
      return false;
    }

    if (!allowedRoles.includes(userRole)) {
      this.router.navigate(['profile']);
      return false;
    }
    return true;
  }
}
