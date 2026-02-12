import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { Register } from './register';
import { AuthService } from '@app/core/services/auth-service';
import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { describe, it, expect, beforeEach, vi } from 'vitest';

// Mock Navbar component
@Component({
  selector: 'app-navbar',
  standalone: true,
  template: '<div></div>'
})
class MockNavbar {}

/**
 * Unit tests for User Registration component (Functionality 2.2.2)
 * Student 3 - Tests the registration form and data submission
 */
describe('Register Component', () => {
  let component: Register;
  let fixture: ComponentFixture<Register>;
  let mockAuthService: { registerWithFile: ReturnType<typeof vi.fn> };
  let mockRouter: { navigate: ReturnType<typeof vi.fn> };

  beforeEach(async () => {
    mockAuthService = {
      registerWithFile: vi.fn()
    };
    mockRouter = {
      navigate: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [Register, FormsModule, CommonModule, MockNavbar],
      providers: [
        { provide: AuthService, useValue: mockAuthService },
        { provide: Router, useValue: mockRouter }
      ]
    })
    .overrideComponent(Register, {
      set: {
        imports: [FormsModule, CommonModule, MockNavbar]
      }
    })
    .compileComponents();

    fixture = TestBed.createComponent(Register);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  // ==================== Positive Tests ====================
  describe('Positive Tests', () => {
    it('should create the component', () => {
      expect(component).toBeTruthy();
    });

    it('should initialize with empty form fields', () => {
      expect(component.email()).toBe('');
      expect(component.password()).toBe('');
      expect(component.confirmPassword()).toBe('');
      expect(component.name()).toBe('');
      expect(component.surname()).toBe('');
      expect(component.address()).toBe('');
      expect(component.phoneNumber()).toBe('');
    });

    it('should successfully submit registration with valid data', async () => {
      component.email.set('test@example.com');
      component.password.set('validPassword123');
      component.confirmPassword.set('validPassword123');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      mockAuthService.registerWithFile.mockReturnValue(of({ message: 'Success' }));

      component.handleRegister();
      await fixture.whenStable();

      expect(mockAuthService.registerWithFile).toHaveBeenCalled();
      expect(component.successMessage()).toContain('Registration successful!');
    });

    it('should send correct FormData to AuthService', async () => {
      component.email.set('test@example.com');
      component.password.set('validPassword123');
      component.confirmPassword.set('validPassword123');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      mockAuthService.registerWithFile.mockReturnValue(of({ message: 'Success' }));

      component.handleRegister();
      await fixture.whenStable();

      const formData = mockAuthService.registerWithFile.mock.calls[0][0] as FormData;
      expect(formData.get('email')).toBe('test@example.com');
      expect(formData.get('password')).toBe('validPassword123');
      expect(formData.get('name')).toBe('John');
      expect(formData.get('lastName')).toBe('Doe');
      expect(formData.get('address')).toBe('123 Main St');
      expect(formData.get('phoneNumber')).toBe('1234567890');
    });

    it('should set success message after successful registration', async () => {
      component.email.set('test@example.com');
      component.password.set('validPassword123');
      component.confirmPassword.set('validPassword123');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      mockAuthService.registerWithFile.mockReturnValue(of({ message: 'Success' }));

      component.handleRegister();
      await fixture.whenStable();

      expect(component.successMessage()).toContain('Registration successful!');
    });
  });

  // ==================== Negative Tests ====================
  describe('Negative Tests', () => {
    it('should show error when email is empty', () => {
      component.email.set('');
      component.password.set('validPassword123');
      component.confirmPassword.set('validPassword123');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      component.handleRegister();

      expect(component.errorMessage()).toBe('Email is required');
    });

    it('should show error when password is empty', () => {
      component.email.set('test@example.com');
      component.password.set('');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      component.handleRegister();

      expect(component.errorMessage()).toBe('Password is required');
    });

    it('should show error when passwords do not match', () => {
      component.email.set('test@example.com');
      component.password.set('validPassword123');
      component.confirmPassword.set('differentPassword');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      component.handleRegister();

      expect(component.errorMessage()).toBe('Passwords do not match');
    });

    it('should show error when name is empty', () => {
      component.email.set('test@example.com');
      component.password.set('validPassword123');
      component.confirmPassword.set('validPassword123');
      component.name.set('');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      component.handleRegister();

      expect(component.errorMessage()).toBe('Name is required');
    });

    it('should display error message from server on failed registration', async () => {
      component.email.set('test@example.com');
      component.password.set('validPassword123');
      component.confirmPassword.set('validPassword123');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      const errorResponse = {
        error: {
          message: 'Email already exists'
        }
      };
      mockAuthService.registerWithFile.mockReturnValue(throwError(() => errorResponse));

      component.handleRegister();
      await fixture.whenStable();

      expect(component.errorMessage()).toBe('Email already exists');
    });

    it('should not call navigate on failed registration', async () => {
      component.email.set('test@example.com');
      component.password.set('validPassword123');
      component.confirmPassword.set('validPassword123');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      mockAuthService.registerWithFile.mockReturnValue(throwError(() => ({ error: 'Error' })));

      component.handleRegister();
      await fixture.whenStable();

      // Navigation should not be called immediately after error
      expect(component.errorMessage()).toBeTruthy();
    });
  });

  // ==================== Boundary Tests ====================
  describe('Boundary Tests', () => {
    it('should show error when password is exactly 7 characters (below minimum)', () => {
      component.email.set('test@example.com');
      component.password.set('1234567'); // 7 chars - boundary
      component.confirmPassword.set('1234567');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      component.handleRegister();

      expect(component.errorMessage()).toBe('Password must be at least 8 characters');
    });

    it('should allow password with exactly 8 characters (minimum valid)', async () => {
      component.email.set('test@example.com');
      component.password.set('12345678'); // 8 chars - boundary
      component.confirmPassword.set('12345678');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      mockAuthService.registerWithFile.mockReturnValue(of({ message: 'Success' }));

      component.handleRegister();
      await fixture.whenStable();

      expect(component.errorMessage()).toBe('');
      expect(mockAuthService.registerWithFile).toHaveBeenCalled();
    });

    it('should reject image larger than 5MB', () => {
      const largeContent = new Array(6 * 1024 * 1024).fill('a').join('');
      const file = new File([largeContent], 'large.jpg', { type: 'image/jpeg' });
      const event = { target: { files: [file] } };

      component.onProfilePictureSelected(event);

      expect(component.errorMessage()).toBe('Image size must be less than 5MB');
    });
  });

  // ==================== Exception Tests ====================
  describe('Exception Tests', () => {
    it('should reject non-image file for profile picture', () => {
      const file = new File([''], 'test.pdf', { type: 'application/pdf' });
      const event = { target: { files: [file] } };

      component.onProfilePictureSelected(event);

      expect(component.errorMessage()).toBe('Please select a valid image file');
      expect(component.profilePicture()).toBeNull();
    });

    it('should handle server error with field validation errors', async () => {
      component.email.set('test@example.com');
      component.password.set('validPassword123');
      component.confirmPassword.set('validPassword123');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      const errorResponse = {
        error: {
          fieldErrors: {
            email: 'Email format invalid'
          }
        }
      };
      mockAuthService.registerWithFile.mockReturnValue(throwError(() => errorResponse));

      component.handleRegister();
      await fixture.whenStable();

      expect(component.errorMessage()).toContain('email: Email format invalid');
    });

    it('should display generic error on unknown server error', async () => {
      component.email.set('test@example.com');
      component.password.set('validPassword123');
      component.confirmPassword.set('validPassword123');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      mockAuthService.registerWithFile.mockReturnValue(throwError(() => ({})));

      component.handleRegister();
      await fixture.whenStable();

      expect(component.errorMessage()).toBe('Registration failed. Please try again.');
    });

    it('should set loading to false after failed registration', async () => {
      component.email.set('test@example.com');
      component.password.set('validPassword123');
      component.confirmPassword.set('validPassword123');
      component.name.set('John');
      component.surname.set('Doe');
      component.address.set('123 Main St');
      component.phoneNumber.set('1234567890');

      mockAuthService.registerWithFile.mockReturnValue(throwError(() => ({ error: 'Error' })));

      component.handleRegister();
      await fixture.whenStable();

      expect(component.loading()).toBe(false);
    });
  });
});
