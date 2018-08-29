/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
/**
 * Angular 2 decorators and services
 */
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { environment } from 'environments/environment';
import { AppState } from './app.service';
import { AuthService } from './services/auth-service';
import { Router } from '@angular/router';


/**
 * App Component
 * Top Level Component
 */
@Component({
  selector: 'app',
  encapsulation: ViewEncapsulation.None,
  styleUrls: [
    './app.component.css',
    '../../node_modules/ngx-simple-modal/dist/styles/simple-modal.css',
    '../../node_modules/leaflet/dist/leaflet.css',
    '../../node_modules/leaflet-sidebar-v2/css/leaflet-sidebar.min.css',
    '../../node_modules/font-awesome/css/font-awesome.min.css',
    '../../node_modules/@ng-select/ng-select/themes/default.theme.css',
    '../../node_modules/bulma/css/bulma.css',
    '../../node_modules/bulma-pageloader/dist/css/bulma-pageloader.min.css'
  ],
  templateUrl: './app.component.html'
})
export class AppComponent {
  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  logout() {
    this.authService.logout();
    this.router.navigateByUrl('/login');
  }

}
