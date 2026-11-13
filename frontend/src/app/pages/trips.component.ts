import { Component, OnInit, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../core/auth.service';
import { TravelService } from '../core/travel.service';
import { Trip } from '../core/models';
import { StateComponent } from '../shared/state.component';

@Component({ selector: 'app-trips', standalone: true, imports: [ReactiveFormsModule, StateComponent, DecimalPipe], template: `<section class="page-heading"><p class="eyebrow">MAKE A PLAN</p><h1>My trips</h1><p>Keep your Chennai ideas close, from first thought to final stop.</p></section><div class="trip-layout"><form class="form-card trip-form" [formGroup]="form" (ngSubmit)="save()"><h2>Add a trail</h2><label>Starting from<input formControlName="source" placeholder="e.g. Bengaluru"></label><label>Travel date<input type="date" formControlName="travelDate"></label><label>Budget (₹)<input type="number" formControlName="budget"></label><label>Transport<select formControlName="transportMode"><option>Train</option><option>Flight</option><option>Bus</option><option>Car</option></select></label>@if(error){<p class="form-error">{{ error }}</p>}<button class="button full" [disabled]="form.invalid || saving">{{ saving ? 'Saving...' : 'Save trip' }}</button></form><div class="trips-list">@if(loading || loadError || !trips.length){<app-state [loading]="loading" [error]="loadError" empty="Your saved trails will appear here." />} @else {@for(trip of trips; track trip.id){<article class="trip-row"><span class="trip-mark">↗</span><div><strong>{{ trip.source }} → {{ trip.destination }}</strong><small>{{ trip.travelDate }} · {{ trip.transportMode }}</small></div><b>₹{{ trip.budget | number:'1.0-0' }}</b></article>}}</div></div>` })
export class TripsComponent implements OnInit {
  private readonly fb = inject(FormBuilder); private readonly auth = inject(AuthService); private readonly service = inject(TravelService);
  form = this.fb.nonNullable.group({ source: ['', Validators.required], travelDate: ['', Validators.required], budget: [0, [Validators.required, Validators.min(1)]], transportMode: ['Train', Validators.required] });
  trips: Trip[] = []; loading = true; saving = false; error = ''; loadError = '';
  ngOnInit(): void { const id = this.auth.user()?.id; if (!id) { this.loading = false; this.loadError = 'Your session does not include a user id. Log in again to view trips.'; return; } this.service.trips(id).subscribe({ next: trips => { this.trips = trips; this.loading = false; }, error: () => { this.loadError = 'Could not load your trips.'; this.loading = false; } }); }
  save(): void { if (this.form.invalid) return; this.saving = true; this.error = ''; this.service.createTrip({ ...this.form.getRawValue(), destination: 'Chennai' }).subscribe({ next: trip => { this.trips = [trip, ...this.trips]; this.form.reset({ source: '', travelDate: '', budget: 0, transportMode: 'Train' }); this.saving = false; }, error: () => { this.error = 'Could not save this trip. Please try again.'; this.saving = false; } }); }
}
