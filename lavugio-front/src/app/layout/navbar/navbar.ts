import { Component } from '@angular/core';
import {Links} from './links/links';
import {Logo} from './logo/logo';
import {Link} from './links/link/link'

@Component({
  selector: 'app-navbar',
  imports: [Links, Logo, Link],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  isMenuOpen:boolean = false;
}
