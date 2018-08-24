/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import {
    Component,
    OnInit
} from '@angular/core';
import { AuthService } from '../../services/auth-service';
import { Router } from '@angular/router';

@Component({
    selector: 'settings',
    providers: [
    ],
    styleUrls: [ './settings.component.scss' ],
    templateUrl: './settings.component.html'
})
export class SettingsComponent implements OnInit {

    constructor(
        public authService: AuthService,
        private router: Router
    ){}

    ngOnInit(): void {
        this.router.navigate(['/settings/users']);
    }
}
