import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UserProfile } from '@app/shared/models/user/userProfile';
import { delay, Observable, of } from 'rxjs';
import { environment } from 'environments/environment';
import {
  EditDriverProfileRequestDTO,
  EditProfileDTO,
} from '@app/shared/models/user/editProfileDTO';
import { LatestRideModel } from '@app/shared/models/ride/latestRide';
import { RideHistoryUserPagingModel } from '@app/shared/models/ride/rideHistoryUserPagingModel';
import { RideHistoryUserDetailedModel } from '@app/shared/models/ride/rideHistoryUserDetailed';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = environment.BACKEND_URL + '/api';

  constructor(private http: HttpClient) {}

  getUserProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/users/profile`);
  }

  updateProfile(updatedProfile: EditProfileDTO): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/users/profile`, updatedProfile);
  }

  sendEditRequest(updatedProfile: EditDriverProfileRequestDTO): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/users/profile/edit-request`, updatedProfile);
  }

  uploadProfilePicture(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    console.log(this.apiUrl + '/users/profile-photo`');
    const nesto = this.http.post<any>(`${this.apiUrl}/users/profile-photo`, formData);

    return nesto;
  }

  changePassword(oldPassword: string, newPassword: string): Observable<any> {
    return this.http.put(
      `${this.apiUrl}/users/change-password`,
      {
        oldPassword,
        newPassword,
      },
      { responseType: 'text' },
    );
  }

  activateAccount(token: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/drivers/activate-account`, {
      token: token,
      password: password,
    });
  }

  validateActivationToken(token: string) {
    return this.http.get<any>(`${this.apiUrl}/users/activate/validate?token=${token}`);
  }

  searchUserEmails(query: string): Observable<{ email: string }[]> {
    if (!query || query.trim().length < 2) {
      return of([]);
    }

    return this.http.get<{ email: string }[]>(
      `${this.apiUrl}/users/email-suggestions?query=${encodeURIComponent(query)}`,
    );
  }

  searchUserEmailsMock(query: string): Observable<{ email: string }[]> {
    const mockData = [
      { email: 'john.doe@example.com' },
      { email: 'john.smith@example.com' },
      { email: 'jane.doe@example.com' },
      { email: 'janedoe123@gmail.com' },
      { email: 'johndoe456@yahoo.com' },
      { email: 'admin@lavugio.com' },
      { email: 'driver1@lavugio.com' },
      { email: 'driver2@lavugio.com' },
      { email: 'passenger@test.com' },
      { email: 'test.user@example.org' },
    ];

    const filtered = mockData.filter((user) =>
      user.email.toLowerCase().includes(query.toLowerCase()),
    );

    return of(filtered).pipe(delay(500));
  }

  blockUser(email: string, reason: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/users/block`, {
      email: email,
      reason: reason,
    });
  }

  isUserBlocked(): Observable<{ blocked: boolean; reason: string }> {
    return this.http.get<{ blocked: boolean; reason: string }>(
      `${this.apiUrl}/users/is-blocked`,
    );
  }

  canUserOrderRide(): Observable<{ isInRide: boolean, block: {blocked: boolean; reason: string } }> {
    return this.http.get<{ isInRide: boolean, block: {blocked: boolean; reason: string } }>(
      `${this.apiUrl}/users/can-order-ride`,
    );
  }

  getLatestRideId(){
    return this.http.get<LatestRideModel>(`${this.apiUrl}/regularUsers/latest-ride`)
  }

  getUserRideHistory(
    page: number,
    pageSize: number,
    sorting: 'ASC' | 'DESC',
    sortBy: 'START' | 'DEPARTURE' | 'DESTINATION',
    startDate: string,
    endDate: string
  ): Observable<RideHistoryUserPagingModel> {
    let params = new HttpParams()
      .set('page', page)
      .set('pageSize', pageSize)
      .set('sorting', sorting)
      .set('sortBy', sortBy)
      .set('startDate', startDate)
      .set('endDate', endDate);
    return this.http.get<RideHistoryUserPagingModel>(`${this.apiUrl}/regularUsers/history`, { params });
  }

  getUserRideHistoryDetailed(rideId: number): Observable<RideHistoryUserDetailedModel> {
    return this.http.get<RideHistoryUserDetailedModel>(`${this.apiUrl}/regularUsers/history/${rideId}`);
  }
}
