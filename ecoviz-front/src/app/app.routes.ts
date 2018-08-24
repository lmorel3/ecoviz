/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import { Routes } from '@angular/router';
import { HomeComponent } from './components/home';
import { MembersComponent } from './components/members';
import { NoContentComponent } from './components/no-content';
import { LoginComponent } from './components/login';

export const ROUTES: Routes = [
  { path: '',      component: HomeComponent },
  { path: 'members', component: MembersComponent },
  { path: 'login', component: LoginComponent },
  { path: 'settings', loadChildren: '../app/modules/settings#SettingsModule' },
  { path: '**',    component: NoContentComponent },
];
