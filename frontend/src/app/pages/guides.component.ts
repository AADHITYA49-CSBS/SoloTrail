import { Component, OnInit, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { TravelService } from '../core/travel.service';
import { TouristGuide } from '../core/models';
import { StateComponent } from '../shared/state.component';

@Component({ selector: 'app-guides', standalone: true, imports: [StateComponent, DecimalPipe], template: `<section class="page-heading"><p class="eyebrow">SEE IT LOCALLY</p><h1>Meet a local</h1><p>The best Chennai stories are told by people who live them.</p></section>@if(loading || error || !items.length){<app-state [loading]="loading" [error]="error" empty="No guides are available right now." />} @else {<div class="card-grid">@for(item of items; track item.id){<article class="content-card guide-card"><div class="avatar">{{ item.guideName.charAt(0) }}</div><h2>{{ item.guideName }}</h2><p>{{ item.specialization || 'Chennai local guide' }}</p><footer><span>{{ item.experienceYears }} years experience</span><strong>₹{{ item.pricePerDay | number:'1.0-0' }} / day</strong></footer></article>}</div>}` })
export class GuidesComponent implements OnInit {
  private readonly service = inject(TravelService); items: TouristGuide[] = []; loading = true; error = '';
  ngOnInit(): void { this.service.guides().subscribe({ next: items => { this.items = items; this.loading = false; }, error: () => { this.error = 'Please check that the backend is running on port 8080.'; this.loading = false; } }); }
}
