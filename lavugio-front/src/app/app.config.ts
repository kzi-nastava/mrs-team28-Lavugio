import { APP_INITIALIZER, ApplicationConfig, inject, provideAppInitializer, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './core/services/auth-interceptor';
import { ErrorInterceptor } from './core/interceptors/error.interceptor';

import { routes } from './app.routes';
import { DriverService } from './core/services/user/driver-service';
import { lastValueFrom } from 'rxjs';
import { AuthService } from './core/services/auth-service';
import { DriverStatusService } from './core/services/driver-status.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: ErrorInterceptor,
      multi: true
    },
    provideAppInitializer(async () => {
      const driverService = inject(DriverService);
      const authService = inject(AuthService);
      const driverStatusService = inject(DriverStatusService);

      if (authService.isDriver()) {
        try {
          const isActive = await lastValueFrom(driverStatusService.isDriverActive());

          if (isActive) {
            await lastValueFrom(driverService.activateDriver());
            console.log('Driver session resumed and tracking started.');
          }
        } catch (err) {
          console.warn('Initialization skipped or failed:', err);
        }
      }
      
    }),
  ]
};
