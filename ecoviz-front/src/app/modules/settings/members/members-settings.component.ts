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

import { Router } from '@angular/router';

import { FormGroup, FormControl, Validators } from '@angular/forms';
import { AuthService } from '../../../services/auth-service';
import { OrganizationService } from '../../../services/organization-service';

@Component({
    selector: 'members-settings',
    providers: [
    ],
    styles: [ '' ],
    templateUrl: './members-settings.component.html'
})
export class MembersSettingsComponent implements OnInit {
  
    private dataForm: FormGroup;

    private badData = false;
    private isImportingOrganizations = false;

    membersCsvFirstLine = '"Name","Billing Street","Billing City","Billing Postal Code","Billing Country","Latitude","Longitude","MemberType"';

    constructor(
        private authService: AuthService,
        private organizationService: OrganizationService,
        private router: Router
    ) {}
  
    public ngOnInit() {
        if(!this.authService.isAdmin()) {
            this.router.navigateByUrl('/');
        }

        this.dataForm = new FormGroup ({
            csvData: new FormControl('', Validators.required),
        });
    }

    importOrganizations() {
        let csvData = this.dataForm.value.csvData;
        if(!csvData.startsWith(this.membersCsvFirstLine)) {
            this.badData = true;
        } else {
            this.isImportingOrganizations = true;
            this.organizationService.importOrganizations(csvData).subscribe(() => {
                this.isImportingOrganizations = false;
            });
            this.badData = false;
        }
    }

}
