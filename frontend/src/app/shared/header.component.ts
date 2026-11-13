import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../core/auth.service';

@Component({
  selector: 'app-header', standalone: true, imports: [RouterLink, RouterLinkActive],
  template: `<header class="header"><a class="brand" routerLink="/">Solo<span>Trail</span></a>
    @if (auth.isLoggedIn()) { <nav><a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">Explore</a><a routerLink="/attractions" routerLinkActive="active">Places</a><a routerLink="/hotels" routerLinkActive="active">Stays</a><a routerLink="/guides" routerLinkActive="active">Guides</a><a routerLink="/trips" routerLinkActive="active">My trips</a></nav><button class="link-button" (click)="logout()">Log out</button> }
    @else { <div class="auth-links"><a routerLink="/login">Log in</a><a class="button small" routerLink="/register">Join SoloTrail</a></div> }
  </header>`
})
export class HeaderComponent {
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  logout(): void { this.auth.logout(); void this.router.navigate(['/login']); }
}
