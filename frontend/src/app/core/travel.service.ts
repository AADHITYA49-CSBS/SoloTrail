import {Injectable,inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Attraction,Hotel,TouristGuide,Trip} from './models';
import {environment} from '../../environments/environment';
@Injectable({providedIn:'root'})
export class TravelService{private http=inject(HttpClient);private base=environment.apiUrl;attractions(){return this.http.get<Attraction[]>(`${this.base}/attractions/city/Chennai`)}hotels(){return this.http.get<Hotel[]>(`${this.base}/hotels/Chennai`)}guides(){return this.http.get<TouristGuide[]>(`${this.base}/guides/Chennai`)}trips(id:number){return this.http.get<Trip[]>(`${this.base}/trips/user/${id}`)}createTrip(t:Trip){return this.http.post<Trip>(`${this.base}/trips`,t)}}
