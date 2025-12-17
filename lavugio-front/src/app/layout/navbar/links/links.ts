import { Component, output } from '@angular/core';
import {Link} from './link/link';
@Component({
  selector: 'app-links',
  imports: [Link],
  templateUrl: './links.html',
  styleUrl: './links.css',
})
export class Links {
  isMenuOpenOutput = output<boolean>();
  isMenuOpen:boolean = false;

  toggleMenu(): void {
    console.log("Menu toggled");
    this.isMenuOpen= !this.isMenuOpen;
    this.isMenuOpenOutput.emit(this.isMenuOpen);
  }
}
