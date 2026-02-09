import { ChangeDetectorRef, Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '@app/core/services/auth-service';
import { MessageBox } from '../message-box/message-box';
import { UserService } from '@app/core/services/user/user-service';
import { UserChatModel } from '@app/shared/models/userChat';
import { timeout } from 'rxjs';

@Component({
  selector: 'app-admin-chat',
  standalone: true,
  imports: [CommonModule, FormsModule, MessageBox],
  templateUrl: './admin-chat.html'
})
export class AdminChatComponent implements OnInit {

  private authService = inject(AuthService);

  private userService = inject(UserService);

  isAdmin = false;

  users = signal<UserChatModel[]>([]);

  selectedUserId: number | null = null;

  private previousUserId: number | null = null;

  private cdr = inject(ChangeDetectorRef);

  ngOnInit(): void {
    this.isAdmin = this.authService.getUserRole() === 'ADMIN';

    if (this.isAdmin) {
      this.loadUsers();
    }
  }

  loadUsers() {
    this.userService.getChattableUsers()
    .pipe(
      timeout(5000)
    )
    .subscribe({
      next: (users) => {
        this.users.set(users);
      },
      error: (err) => {
        console.error('Request failed or timed out', err);
      }
    })
  }

  onUserChange() {
    if (this.selectedUserId !== this.previousUserId) {
      
      const tempId = this.selectedUserId;
      this.selectedUserId = null;
      
      setTimeout(() => {
        this.selectedUserId = tempId;
        this.previousUserId = tempId;
        this.cdr.detectChanges();
      }, 0);
    }
  }
}
