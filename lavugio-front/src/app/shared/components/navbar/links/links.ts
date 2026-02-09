import { Component, output, input, inject, WritableSignal, signal } from '@angular/core';
import { Link } from './link/link';
import { RouterLink } from "@angular/router";
import { CommonModule } from '@angular/common';
import { UserService } from '@app/core/services/user/user-service';
import { timeout } from 'rxjs';

@Component({
  selector: 'app-links',
  imports: [Link, RouterLink, CommonModule],
  templateUrl: './links.html',
  styleUrl: './links.css',
})
export class Links {
  regularUserService = inject(UserService);
  isMenuOpenOutput = output<boolean>();
  logoutOutput = output<void>();
  toggleStatusOutput = output<void>();
  isMenuOpen: boolean = false;
  hasLatestRide = input(false);
  latestRideId = input(0);
  latestRideStatus = input("");
  
  isAuthenticated = input<boolean>(false);
  userName = input<string>('');
  isAdmin = input<boolean>(false);
  isDriver = input<boolean>(false);
  isRegularUser = input<boolean>(false);
  driverActive = input<boolean>(false);
  statusLoading = input<boolean>(false);

  hasNewNotifications = input(false);


  constructor(){
  }

  toggleMenu(): void {
    console.log("Menu toggled");
    this.isMenuOpen = !this.isMenuOpen;
    this.isMenuOpenOutput.emit(this.isMenuOpen);
  }

  onLogout(): void {
    this.logoutOutput.emit();
  }

  onToggleStatus(): void {
    this.toggleStatusOutput.emit();
  }
}
