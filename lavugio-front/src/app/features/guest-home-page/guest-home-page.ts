import { Component } from '@angular/core';
import { MapComponent } from "@app/shared/components/map/map";
import {FormComponent} from "./form/form";
import {Button} from "@app/shared/components/button/button";
import { Router } from '@angular/router';

@Component({
  selector: 'app-guest-home-page',
  imports: [MapComponent, FormComponent, Button],
  templateUrl: './guest-home-page.html',
  styleUrl: './guest-home-page.css',
})
export class GuestHomePage {
  constructor(private router: Router) {}

  sendToRegistrationPage() {
    this.router.navigate(['/register']); // ← ovde ide ruta
  }
  scrollDown() {
    const screenHeight = window.innerHeight; // visina ekrana
    window.scrollTo({ top: screenHeight, behavior: 'smooth' });
  }
}
