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
import { ProjectService } from '../../../services/project-service';

@Component({
    selector: 'projects-settings',
    providers: [
    ],
    styles: [ '' ],
    templateUrl: './projects-settings.component.html'
})
export class ProjectsSettingsComponent implements OnInit {
  
    private dataForm: FormGroup;

    private badData = false;
    private isUpdatingProjects = false;

    projectsCsvFirstLine = 'PROJECT,MEMBERSHIP,NAME,COUNTRY_CODE,ROLE,ADDRESS,CITY,POSTCODE,COUNTRY,LATITUDE,LONGITUDE,OSM_ID,TAGS';    

    constructor(
        private authService: AuthService,
        private projectService: ProjectService,
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
    
    importProjects() {
        let csvData = this.dataForm.value.csvData;
        if(!csvData.startsWith(this.projectsCsvFirstLine)) {
            this.badData = true;
        } else {
            this.isUpdatingProjects = true
            this.projectService.importProjects(csvData).subscribe(() => { this.isUpdatingProjects = false });
            this.badData = false;
        }
    }
    
    exportProjects() {
        this.projectService.exportProjects('csv');
    }

}
