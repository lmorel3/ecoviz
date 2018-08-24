/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { routes } from './settings.routes';
import { SettingsComponent } from './settings.component';
import { UsersSettingsComponent } from './users';
import { ProjectsSettingsComponent } from './projects';
import { MembersSettingsComponent } from './members';

@NgModule({
  declarations: [
    SettingsComponent,
    UsersSettingsComponent,
    ProjectsSettingsComponent,
    MembersSettingsComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
  ],
})
export class SettingsModule {
  public static routes = routes;
}
