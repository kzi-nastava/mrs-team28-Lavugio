import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Navbar } from '@app/shared/components/navbar/navbar';

@Component({
  selector: 'app-admin-panel',
  imports: [Navbar],
  templateUrl: './admin-panel.html',
  styleUrl: './admin-panel.css',
})
export class AdminPanel {

  private router = inject(Router);

  openDriverRegistration() {
    this.router.navigate(['/register-driver']);
  }

  openDriverUpdateRequest() {
    this.router.navigate(['/driver-update-requests']);
  }

  openBlockUser() {
    alert('Not implemented yet')
  }

  openReportsView() {
    alert('Not implemented yet')
  }

}
