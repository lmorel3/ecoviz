/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import { UsersSettingsComponent } from './users';
import { ProjectsSettingsComponent } from './projects';
import { MembersSettingsComponent } from './members';
import { SettingsComponent } from './settings.component';

export const routes = [
  { path: '',      component: SettingsComponent, children: [
      { path: 'projects', component: ProjectsSettingsComponent },
      { path: 'members', component: MembersSettingsComponent },
      { path: 'users',    component: UsersSettingsComponent }
    ] 
  }
];
