import {Component,OnInit,inject} from '@angular/core';
import {TravelService} from '../core/travel.service';
import {Restaurant} from '../core/models';
import {StateComponent} from '../shared/state.component';

@Component({selector:'app-restaurants',standalone:true,imports:[StateComponent],template:`<section class="heading"><small>EAT LIKE A LOCAL</small><h1>Tables worth finding</h1><p>From classic tiffin to coastal seafood, make room for Chennai's best flavours.</p></section>@if(loading||error||!items.length){<app-state [loading]="loading" [error]="error" empty="No restaurants available."/>}@else{<div class="cards">@for(item of items;track item.id){<article><span>{{item.cuisine}}</span><h2>{{item.restaurantName}}</h2><p>{{item.description}}</p><footer>★ {{item.rating||'—'}} · {{item.priceRange}} · {{item.openNow?'Open now':'Check timings'}}</footer></article>}</div>}`})
export class RestaurantsComponent implements OnInit{private service=inject(TravelService);items:Restaurant[]=[];loading=true;error='';ngOnInit(){this.service.restaurants().subscribe({next:x=>{this.items=x;this.loading=false},error:()=>{this.error='Start the backend on port 8080.';this.loading=false}})}}
