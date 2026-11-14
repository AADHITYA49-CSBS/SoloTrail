import {Component,OnInit,inject} from '@angular/core';
import {DecimalPipe} from '@angular/common';
import {FormBuilder,ReactiveFormsModule,Validators} from '@angular/forms';
import {forkJoin} from 'rxjs';
import {AuthService} from '../core/auth.service';
import {BookingService} from '../core/booking.service';
import {TravelService} from '../core/travel.service';
import {Booking,BookingRequest,Hotel,Restaurant,TouristGuide,Trip} from '../core/models';
import {StateComponent} from '../shared/state.component';

@Component({
  selector:'app-trips',
  standalone:true,
  imports:[ReactiveFormsModule,StateComponent,DecimalPipe],
  template:`<section class="heading"><small>MAKE A PLAN</small><h1>Plan your Chennai trail</h1><p>Tell us where you are coming from, choose transport, then select the services you want in one demo checkout.</p></section><div class="trip-layout"><form class="form" [formGroup]="form" (ngSubmit)="savePlan()"><h2>Trip details</h2><label>Starting from<input formControlName="source" placeholder="Bengaluru"></label><label>Travel date<input type="date" formControlName="travelDate"></label><label>Transport<select formControlName="transportMode"><option>Bus</option><option>Train</option><option>Car</option><option>Flight</option></select></label><p class="fare-hint">Estimated transport: ₹{{transportFare}}</p><label>Budget (₹)<input type="number" formControlName="budget" min="1"></label><p class="error">{{error}}</p><button class="button full" [disabled]="form.invalid||saving">{{saving?'Preparing...':'Show options'}}</button></form><div>@if(loading||loadError){<app-state [loading]="loading" [error]="loadError" empty=""/>}@else{<div class="selection-panel"><h2>Choose your services</h2><p class="muted">Selections are optional. The final amount is calculated by the backend.</p><h3>Stay</h3><div class="choice-grid">@for(hotel of hotels;track hotel.id){<button type="button" class="choice" [class.selected]="selectedHotel?.id===hotel.id" (click)="selectedHotel=hotel"><b>{{hotel.hotelName}}</b><span>₹{{hotel.pricePerNight|number:'1.0-0'}} / night · ★ {{hotel.rating}}</span></button>}</div><h3>Food</h3><div class="choice-grid">@for(restaurant of restaurants;track restaurant.id){<button type="button" class="choice" [class.selected]="selectedRestaurant?.id===restaurant.id" (click)="selectedRestaurant=restaurant"><b>{{restaurant.restaurantName}}</b><span>{{restaurant.cuisine}} · {{restaurant.priceRange}}</span></button>}</div><h3>Guide</h3><div class="choice-grid">@for(guide of guides;track guide.id){<button type="button" class="choice" [class.selected]="selectedGuide?.id===guide.id" (click)="selectedGuide=guide"><b>{{guide.guideName}}</b><span>₹{{guide.pricePerDay|number:'1.0-0'}} / day · {{guide.specialization}}</span></button>}</div><div class="checkout"><p><b>Estimated total:</b> ₹{{estimatedTotal|number:'1.0-0'}}</p><button class="button" [disabled]="!activeTrip||checkingOut" (click)="checkout()">{{checkingOut?'Processing...':'Pay ₹'+(estimatedTotal|number:'1.0-0')+' (Demo)'}}</button><p class="muted">This is a simulated payment. No real money is charged.</p></div></div>}</div></div><section class="saved-trips"><h2>Your saved plans</h2>@if(trips.length){@for(trip of trips;track trip.id){<article class="trip">{{trip.source}} → {{trip.destination}}<small>{{trip.travelDate}} · {{trip.transportMode}} · ₹{{trip.budget|number:'1.0-0'}}</small></article>}}@else if(!loading){<p class="muted">Your saved plans appear here after you create one.</p>}</section>@if(booking){<div class="confirmation"><h2>Demo booking confirmed</h2><p>Your reference is <b>{{booking.bookingReference}}</b>.</p><p>Total paid in simulation: <b>₹{{booking.totalAmount|number:'1.0-0'}}</b></p><p class="muted">Keep this reference for your interview demo. No real ticket or payment was issued.</p></div>}`
})
export class TripsComponent implements OnInit{
  private fb=inject(FormBuilder);
  private auth=inject(AuthService);
  private service=inject(TravelService);
  private bookingService=inject(BookingService);
  form=this.fb.nonNullable.group({source:['',[Validators.required]],travelDate:['',[Validators.required]],budget:[0,[Validators.required,Validators.min(1)]],transportMode:['Bus',[Validators.required]]});
  trips:Trip[]=[];
  hotels:Hotel[]=[];
  restaurants:Restaurant[]=[];
  guides:TouristGuide[]=[];
  selectedHotel:Hotel|null=null;
  selectedRestaurant:Restaurant|null=null;
  selectedGuide:TouristGuide|null=null;
  activeTrip:Trip|null=null;
  booking:Booking|null=null;
  loading=true;
  saving=false;
  checkingOut=false;
  error='';
  loadError='';
  transportFares:{[key:string]:number}={Bus:800,Train:1200,Car:2500,Flight:4500};
  ngOnInit(){
    const id=this.auth.user()?.id;
    if(typeof id!=='number'){this.loading=false;this.loadError='Please log in again before planning a trip.';return}
    forkJoin({trips:this.service.trips(id),hotels:this.service.hotels(),restaurants:this.service.restaurants(),guides:this.service.guides()}).subscribe({
      next:data=>{this.trips=data.trips;this.hotels=data.hotels;this.restaurants=data.restaurants;this.guides=data.guides;this.loading=false},
      error:()=>{this.loadError='Could not load planning options. Check that the backend is running.';this.loading=false}
    });
  }
  get transportFare(){return this.transportFares[this.form.controls.transportMode.value]||0}
  get estimatedTotal(){
    const restaurantCost=this.selectedRestaurant?.priceRange?.length===1?300:this.selectedRestaurant?.priceRange?.length===2?600:1000;
    return this.transportFare+(this.selectedHotel?.pricePerNight||0)+(this.selectedGuide?.pricePerDay||0)+(this.selectedRestaurant?restaurantCost:0);
  }
  savePlan(){
    if(this.form.invalid)return;
    this.saving=true;this.error='';this.booking=null;
    const trip:Trip={...this.form.getRawValue(),destination:'Chennai'};
    this.service.createTrip(trip).subscribe({
      next:created=>{this.activeTrip=created;this.trips=[created,...this.trips];this.saving=false},
      error:()=>{this.error='Could not save your plan.';this.saving=false}
    });
  }
  checkout(){
    if(!this.activeTrip||this.checkingOut)return;
    this.checkingOut=true;this.error='';
    const request:BookingRequest={source:this.activeTrip.source,destination:this.activeTrip.destination,travelDate:this.activeTrip.travelDate,transportMode:this.activeTrip.transportMode,hotelId:this.selectedHotel?.id,restaurantId:this.selectedRestaurant?.id,guideId:this.selectedGuide?.id};
    this.bookingService.checkout(request).subscribe({
      next:booking=>{this.booking=booking;this.checkingOut=false},
      error:()=>{this.error='Could not complete the demo checkout.';this.checkingOut=false}
    });
  }
}
