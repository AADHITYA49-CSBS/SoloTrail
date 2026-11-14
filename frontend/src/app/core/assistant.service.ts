import {Injectable,inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ChatResponse} from './models';
import {environment} from '../../environments/environment';

@Injectable({providedIn:'root'})
export class AssistantService{
  private http=inject(HttpClient);
  chat(message:string){return this.http.post<ChatResponse>(`${environment.apiUrl}/assistant/chat`,{message})}
}
