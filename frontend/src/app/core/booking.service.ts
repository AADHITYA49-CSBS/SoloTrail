import {Injectable,inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Booking,BookingRequest} from './models';
import {environment} from '../../environments/environment';

@Injectable({providedIn:'root'})
export class BookingService{
  private http=inject(HttpClient);
  checkout(request:BookingRequest){return this.http.post<Booking>(`${environment.apiUrl}/bookings/checkout`,request)}
  bookings(userId:number){return this.http.get<Booking[]>(`${environment.apiUrl}/bookings/user/${userId}`)}
}
