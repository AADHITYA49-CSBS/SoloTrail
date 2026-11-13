import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Attraction, Hotel, TouristGuide, Trip } from './models';

@Injectable({ providedIn: 'root' })
export class TravelService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:8080/api';
  attractions() { return this.http.get<Attraction[]>(`${this.baseUrl}/attractions/city/Chennai`); }
  hotels() { return this.http.get<Hotel[]>(`${this.baseUrl}/hotels/Chennai`); }
  guides() { return this.http.get<TouristGuide[]>(`${this.baseUrl}/guides/Chennai`); }
  trips(userId: number) { return this.http.get<Trip[]>(`${this.baseUrl}/trips/user/${userId}`); }
  createTrip(trip: Trip) { return this.http.post<Trip>(`${this.baseUrl}/trips`, trip); }
}
