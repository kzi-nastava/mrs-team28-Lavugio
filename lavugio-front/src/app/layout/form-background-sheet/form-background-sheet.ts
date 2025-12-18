import { NgTemplateOutlet } from '@angular/common';
import { Component, Input, TemplateRef, ContentChild } from '@angular/core';

@Component({
  selector: 'app-form-background-sheet',
  imports: [NgTemplateOutlet],
  templateUrl: './form-background-sheet.html',
  styleUrl: './form-background-sheet.css',
})
export class FormBackgroundSheet {
  @Input() title = '';
  isPanelOpen = false;

    @ContentChild('contentTemplate', { static: true }) 
    contentTemplate!: TemplateRef<any>;

  togglePanel() {
    this.isPanelOpen = !this.isPanelOpen;
  }
}
