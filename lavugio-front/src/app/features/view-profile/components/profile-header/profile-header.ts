import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-profile-header',
  imports: [],
  templateUrl: './profile-header.html',
  styleUrl: './profile-header.css',
})
export class ProfileHeader {
  @Input() name = "Pera";
  @Input() surname = "Peric";
  @Input() email = "pera@peric.com";
  @Input() avatarUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTfE8XWOVe86hLGi8m9mgPTsva_KWjTHbT9iQ&s";
}
