import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({ selector: 'app-register', standalone: true, imports: [ReactiveFormsModule, RouterLink], template: `<div class="auth-page"><div class="auth-copy"><p class="eyebrow">MAKE IT YOURS</p><h1>A city full of<br><em>possibility.</em></h1><p>Save the places that spark something and make every Chennai day count.</p></div><form class="form-card" [formGroup]="form" (ngSubmit)="submit()"><h2>Create account</h2><p class="muted">Start your personal trail.</p><label>Name<input formControlName="name" placeholder="Your name"></label><label>Email<input type="email" formControlName="email" placeholder="you@example.com"></label><label>Password<input type="password" formControlName="password" placeholder="At least 6 characters"></label>@if(error){<p class="form-error">{{ error }}</p>}<button class="button full" [disabled]="form.invalid || loading">{{ loading ? 'Creating...' : 'Create account' }}</button><p class="form-footer">Already a member? <a routerLink="/login">Log in</a></p></form></div>` })
export class RegisterComponent {
  private readonly fb = inject(FormBuilder); private readonly auth = inject(AuthService); private readonly router = inject(Router);
  form = this.fb.nonNullable.group({ name: ['', Validators.required], email: ['', [Validators.required, Validators.email]], password: ['', [Validators.required, Validators.minLength(6)]] }); loading = false; error = '';
  submit(): void { if (this.form.invalid) return; this.loading = true; this.auth.register(this.form.getRawValue()).subscribe({ next: () => void this.router.navigate(['/login']), error: () => { this.error = 'We could not create your account. Please try again.'; this.loading = false; } }); }
}
