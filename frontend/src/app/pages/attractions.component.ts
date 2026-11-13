import { Component, OnInit, inject } from '@angular/core';
import { TravelService } from '../core/travel.service';
import { Attraction } from '../core/models';
import { StateComponent } from '../shared/state.component';

@Component({ selector: 'app-attractions', standalone: true, imports: [StateComponent], template: `<section class="page-heading"><p class="eyebrow">GO SLOW</p><h1>Places to wander</h1><p>Chennai is best discovered one story, street and sunset at a time.</p></section>@if(loading || error || !items.length){<app-state [loading]="loading" [error]="error" empty="No attractions have been added yet." />} @else {<div class="card-grid">@for(item of items; track item.id){<article class="content-card"><div class="card-top"><span class="tag">{{ item.category }}</span><span class="rating">★ {{ item.rating || '—' }}</span></div><h2>{{ item.attractionName }}</h2><p>{{ item.description || 'A Chennai favourite waiting to be explored.' }}</p><footer><span>{{ item.openNow ? '● Open now' : '○ Check timings' }}</span><span>{{ item.city }}</span></footer></article>}</div>}` })
export class AttractionsComponent implements OnInit {
  private readonly service = inject(TravelService); items: Attraction[] = []; loading = true; error = '';
  ngOnInit(): void { this.service.attractions().subscribe({ next: items => { this.items = items; this.loading = false; }, error: () => { this.error = 'Please check that the backend is running on port 8080.'; this.loading = false; } }); }
}
