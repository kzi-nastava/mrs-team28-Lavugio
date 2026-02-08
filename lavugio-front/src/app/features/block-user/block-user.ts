import { Component, inject, signal } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { WhiteSheetBackground } from '@app/shared/components/white-sheet-background/white-sheet-background';
import { UserEmailInput } from './components/user-email-input/user-email-input';
import { DialogService } from '@app/core/services/dialog-service';
import { FormsModule } from '@angular/forms';
import { UserService } from '@app/core/services/user/user-service';
import { LiveSupportButtonComponent } from "@app/shared/components/live-support-button/live-support-button";

@Component({
  selector: 'app-block-user',
  imports: [WhiteSheetBackground, Navbar, UserEmailInput, FormsModule, LiveSupportButtonComponent],
  templateUrl: './block-user.html',
  styleUrl: './block-user.css',
})
export class BlockUser {
  userService = inject(UserService);

  selectedEmail = signal('');
  blockReason = signal('');

  onEmailSelected(event: { email: string }) {
  }

  onEmailChange(event: string) {
    console.log('Email input changed:', event);
    this.selectedEmail.set(event);
  }

  dialogService = inject(DialogService);

  blockUser() {
    this.dialogService.openConfirm("Confirm Block", "Are you sure you want to block this user?")
    .subscribe((confirmed) => {
      if (confirmed) {
        this.userService.blockUser(this.selectedEmail(), this.blockReason()).subscribe({
          next: () => {
            this.dialogService.open("Success", "User has been blocked successfully.", false);
          },
          error: (err) => {
            console.log(err); 
            this.dialogService.open("Error", "Failed to block user: " + err.error.message, true);
          }
        });
        this.selectedEmail.set('');
        this.blockReason.set('');
      } else {
        console.log('User block cancelled');
      }
    });
  }
}
