import { Component, Input, input, InputSignal } from '@angular/core';

@Component({
  selector: 'app-white-sheet-background',
  imports: [],
  templateUrl: './white-sheet-background.html',
  styleUrl: './white-sheet-background.css',
})
export class WhiteSheetBackground {
  title: InputSignal<string> = input<string>('');

  width: InputSignal<'sm' | 'md' | 'lg'> = input<'sm' | 'md' | 'lg'>('sm');
 
  spacing: InputSignal<'center' | 'between'> = input<'center' | 'between'>('center');

  get maxWidthClass(): string {
    const widthMap = {
      'sm': 'md:max-w-2xl',
      'md': 'md:max-w-3xl',
      'lg': 'md:max-w-5xl'
    };
    return widthMap[this.width()];
  }

  get justifyClass(): string {
    const justifyMap = {
      'center': 'justify-center',
      'between': 'justify-between'
    };
    return justifyMap[this.spacing()];
  }
}
