import { Component, Input, inject } from '@angular/core';
import { ProfileEdit } from '../../services/profile-edit';

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

  editService = inject(ProfileEdit);
  previewUrl: string | null = null;

  onAvatarClick(fileInput: HTMLInputElement) {
    if (this.editService.isEditMode()) {
      fileInput.click();
    }
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    const file = input.files && input.files[0];
    if (!file) return;
    if (!file.type.startsWith('image/')) return;

    const reader = new FileReader();
    reader.onload = () => {
      this.previewUrl = reader.result as string;
    };
    reader.readAsDataURL(file);
  }
}
