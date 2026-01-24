import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DriverRegistration } from '@app/shared/models/user/driverRegistration';
import { EditDriverProfileRequestDTO } from '@app/shared/models/user/editProfileDTO';
import { UserProfile } from '@app/shared/models/user/userProfile';

@Injectable({
  providedIn: 'root',
})
export class DriverService {
  private apiUrl: string = 'http://localhost:8080/api/drivers';

  constructor(private http: HttpClient) {}

  registerDriver(data: DriverRegistration) {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  sendEditRequest(updatedProfile: EditDriverProfileRequestDTO) {
    return this.http.post<any>(`${this.apiUrl}/edit-request`, updatedProfile);
  }
}