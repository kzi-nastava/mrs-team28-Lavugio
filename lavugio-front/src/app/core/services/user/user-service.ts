import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserProfile } from '@app/shared/models/user/userProfile';
import { Observable } from 'rxjs';
import { environment } from 'environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = environment.BACKEND_URL;

  constructor(private http: HttpClient) {}

  getUserProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/users/profile`);
  }

  updateProfile(updatedProfile: UserProfile): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/users/profile`, updatedProfile);
  }

  uploadProfilePicture(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    console.log(this.apiUrl + "/users/profile-photo`");    
    const nesto = this.http.post<any>(`${this.apiUrl}/users/profile-photo`, formData);

    return nesto;
  }

  changePassword(oldPassword: string, newPassword: string): Observable<any> {
    return this.http.put(`${this.apiUrl}/users/change-password`, {
      oldPassword,
      newPassword
    }, { responseType: 'text' });
  }
}
