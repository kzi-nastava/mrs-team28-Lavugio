import { Component, inject, signal } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { WhiteSheetBackground } from '@app/shared/components/white-sheet-background/white-sheet-background';
import { UserEmailInput } from './components/user-email-input/user-email-input';
import { DialogService } from '@app/core/services/dialog-service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-block-user',
  imports: [WhiteSheetBackground, Navbar, UserEmailInput, FormsModule],
  templateUrl: './block-user.html',
  styleUrl: './block-user.css',
})
export class BlockUser {
  selectedEmail = signal('');
  blockReason = signal('');

  onEmailSelected(event: { email: string }) {
    console.log('User email selected for blocking:', event.email);
    // Implement blocking logic here
  }

  onEmailChange(event: string) {
    console.log('Email input changed:', event);
    this.selectedEmail.set(event);
  }

  dialogService = inject(DialogService);

  blockUser() {
    this.dialogService.openBlocked("Your account has been blocked due to suspicious activity.");
    /*this.dialogService.openConfirm("Confirm Block", "Are you sure you want to block this user?")
    .subscribe((confirmed) => {
      if (confirmed) {
        alert('Blocking user: ' + this.selectedEmail());
        alert('Reason: ' + this.blockReason());
        this.selectedEmail.set('');
        this.blockReason.set('');
        // TODO: Call your service here with this.selectedEmail() and this.blockReason()
      } else {
        alert('User block action was cancelled.');
      }
    });*/
  }
}
