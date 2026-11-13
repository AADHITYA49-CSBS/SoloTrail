import { Component, OnInit, inject } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { TravelService } from '../core/travel.service';
import { Hotel } from '../core/models';
import { StateComponent } from '../shared/state.component';

@Component({ selector: 'app-hotels', standalone: true, imports: [StateComponent, DecimalPipe], template: `<section class="page-heading"><p class="eyebrow">STAY A LITTLE LONGER</p><h1>Stays with character</h1><p>Find a comfortable base for your Chennai adventure.</p></section>@if(loading || error || !items.length){<app-state [loading]="loading" [error]="error" empty="No stays have been added yet." />} @else {<div class="card-grid">@for(item of items; track item.id){<article class="content-card"><div class="card-top"><span class="tag">{{ item.available ? 'Available' : 'Ask for availability' }}</span><span class="rating">★ {{ item.rating || '—' }}</span></div><h2>{{ item.hotelName }}</h2><p>{{ item.description || 'A welcoming stay in Chennai.' }}</p><footer><strong>₹{{ item.pricePerNight | number:'1.0-0' }} <small>/ night</small></strong><span>{{ item.city }}</span></footer></article>}</div>}` })
export class HotelsComponent implements OnInit {
  private readonly service = inject(TravelService); items: Hotel[] = []; loading = true; error = '';
  ngOnInit(): void { this.service.hotels().subscribe({ next: items => { this.items = items; this.loading = false; }, error: () => { this.error = 'Please check that the backend is running on port 8080.'; this.loading = false; } }); }
}
