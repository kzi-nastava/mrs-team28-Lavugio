import { Component } from '@angular/core';
import {Navbar} from '@app/shared/components/navbar/navbar';
import { LiveSupportButtonComponent } from "@app/shared/components/live-support-button/live-support-button";
@Component({
  selector: 'app-base-info-page',
  imports: [Navbar, LiveSupportButtonComponent],
  templateUrl: './base-info-page.html',
  styleUrl: './base-info-page.css',
})
export class BaseInfoPage {

}
