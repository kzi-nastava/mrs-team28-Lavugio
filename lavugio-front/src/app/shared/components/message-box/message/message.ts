import { CommonModule } from '@angular/common';
import { Component, inject, input, OnInit, signal } from '@angular/core';
import { AuthService } from '@app/core/services/auth-service';

@Component({
  selector: 'app-message',
  imports: [CommonModule],
  templateUrl: './message.html',
  styleUrl: './message.css',
})
export class Message implements OnInit{
  authService = inject(AuthService);

  senderId = input(0);
  text = input('');
  userId = this.authService.getUserId();
  isSender: boolean = false;

  ngOnInit(){
    this.checkIfSender();
  }

  checkIfSender(){
    if (this.authService.getUserRole() == "ADMIN"){
      if (this.senderId() == 0){
        this.isSender = true;
      }
    }
    else if (this.senderId() == this.userId){
      this.isSender = true;
    }
  }
}
