import { Component, Input } from '@angular/core';

@Component({ selector: 'app-state', standalone: true, template: `<div class="state" [class.error]="error"><strong>{{ error ? 'Could not load this yet' : (loading ? 'Finding the best of Chennai...' : 'Nothing here yet') }}</strong><p>{{ error || (loading ? 'Just a moment.' : empty) }}</p></div>` })
export class StateComponent { @Input() loading = false; @Input() error = ''; @Input() empty = 'Try again later.'; }
