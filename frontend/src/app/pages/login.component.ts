import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({ selector: 'app-login', standalone: true, imports: [ReactiveFormsModule, RouterLink], template: `<div class="auth-page"><div class="auth-copy"><p class="eyebrow">WELCOME BACK</p><h1>Your next trail<br><em>starts here.</em></h1><p>Pick up where you left off and keep exploring Chennai at your own pace.</p></div><form class="form-card" [formGroup]="form" (ngSubmit)="submit()"><h2>Log in</h2><p class="muted">Sign in to your SoloTrail.</p><label>Email<input type="email" formControlName="email" placeholder="you@example.com"></label><label>Password<input type="password" formControlName="password" placeholder="Your password"></label>@if(error){<p class="form-error">{{ error }}</p>}<button class="button full" [disabled]="form.invalid || loading">{{ loading ? 'Signing in...' : 'Continue' }}</button><p class="form-footer">New here? <a routerLink="/register">Create an account</a></p></form></div>` })
export class LoginComponent {
  private readonly fb = inject(FormBuilder); private readonly auth = inject(AuthService); private readonly router = inject(Router);
  form = this.fb.nonNullable.group({ email: ['', [Validators.required, Validators.email]], password: ['', Validators.required] }); loading = false; error = '';
  submit(): void { if (this.form.invalid) return; this.loading = true; this.error = ''; this.auth.login(this.form.getRawValue()).subscribe({ next: () => void this.router.navigate(['/']), error: () => { this.error = 'Check your email and password, then try again.'; this.loading = false; } }); }
}
