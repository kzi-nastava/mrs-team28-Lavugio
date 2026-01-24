import { Component } from '@angular/core';
import { Navbar } from '@app/shared/components/navbar/navbar';
import { WhiteSheetBackground } from '@app/shared/components/white-sheet-background/white-sheet-background';
import { UserEmailInput } from './components/user-email-input/user-email-input';

@Component({
  selector: 'app-block-user',
  imports: [WhiteSheetBackground, Navbar, UserEmailInput],
  templateUrl: './block-user.html',
  styleUrl: './block-user.css',
})
export class BlockUser {
  selectedEmail: string = '';

  onEmailSelected(event: { email: string }) {
    console.log('User email selected for blocking:', event.email);
    // Implement blocking logic here
  }

  onEmailChange(event: string) {
    console.log('Email input changed:', event);
    this.selectedEmail = event;
  }
}
