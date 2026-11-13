import {Injectable,inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable,tap} from 'rxjs';
import {AuthUser} from './models';
@Injectable({providedIn:'root'})
export class AuthService{
 private http=inject(HttpClient); private base='/api/auth';
 register(data:{name:string;email:string;password:string}):Observable<string>{return this.http.post(`${this.base}/register`,data,{responseType:'text'})}
 login(data:{email:string;password:string}):Observable<string>{return this.http.post(`${this.base}/login`,data,{responseType:'text'}).pipe(tap(t=>this.session(t)))}
 private session(value:string){const token=value.replace(/^"|"$/g,'');localStorage.setItem('solotrail_token',token);try{const p=JSON.parse(atob(token.split('.')[1])) as Record<string,unknown>;localStorage.setItem('solotrail_user',JSON.stringify({id:p['userId']??p['id'],email:p['sub']}))}catch{}}
 setUserId(id:number){const current=this.user()??{};localStorage.setItem('solotrail_user',JSON.stringify({...current,id}))}
 token(){return localStorage.getItem('solotrail_token')} user():AuthUser|null{const v=localStorage.getItem('solotrail_user');return v?JSON.parse(v) as AuthUser:null} isLoggedIn(){return !!this.token()} logout(){localStorage.removeItem('solotrail_token');localStorage.removeItem('solotrail_user')}
}
