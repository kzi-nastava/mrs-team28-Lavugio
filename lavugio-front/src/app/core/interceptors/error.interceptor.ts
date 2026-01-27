import { Injectable, inject } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
  HttpErrorResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { NotificationService } from '@app/core/services/notification-service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  private router = inject(Router);
  private notificationService = inject(NotificationService);

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    return next.handle(request).pipe(
      catchError((error: HttpErrorResponse) => {
        const url = request.url;
        
        const authEndpoints = ['/login', '/register', '/verify-email', '/forgot-password', '/reset-password'];
        const isAuthEndpoint = authEndpoints.some(endpoint => url.includes(endpoint));
        
        // Don't redirect for auth endpoints or logout endpoint
        const isLogoutEndpoint = url.includes('/logout');
        
        if (isAuthEndpoint || isLogoutEndpoint) {
          return throwError(() => error);
        }
        
        if (error.status === 401) {
          this.router.navigate(['/login']);
        } else if (error.status === 403) {
          this.router.navigate(['/']);
        }
        return throwError(() => error);
      })
    );
  }
}
