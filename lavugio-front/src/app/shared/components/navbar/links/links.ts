import { Component, output, input } from '@angular/core';
import { Link } from './link/link';
import { RouterLink } from "@angular/router";
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-links',
  imports: [Link, RouterLink, CommonModule],
  templateUrl: './links.html',
  styleUrl: './links.css',
})
export class Links {
  isMenuOpenOutput = output<boolean>();
  logoutOutput = output<void>();
  toggleStatusOutput = output<void>();
  isMenuOpen: boolean = false;
  
  isAuthenticated = input<boolean>(false);
  userName = input<string>('');
  isAdmin = input<boolean>(false);
  isDriver = input<boolean>(false);
  driverActive = input<boolean>(false);
  statusLoading = input<boolean>(false);

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
