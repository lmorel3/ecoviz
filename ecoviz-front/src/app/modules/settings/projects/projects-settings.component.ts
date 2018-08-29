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
    private progress: number = 0;

    projectsCsvFirstLine = 'PROJECT,MEMBERSHIP,NAME,COUNTRY_CODE,ROLE,ADDRESS,CITY,POSTCODE,COUNTRY,LATITUDE,LONGITUDE,TAGS';    

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
            this.isUpdatingProjects = true;
            this.projectService.importProjects(csvData).subscribe((resp: any) => this.getProgress(resp.value) );
            this.badData = false;
        }
    }

    getProgress(id: string) {
        let that = this;
        this.projectService.getImportProgress(id).subscribe((resp: any) => {
            let progress = resp.value

            if(progress >= 0 && progress < 100) {
                that.progress = progress;
                setTimeout(() => { that.getProgress(id) }, 5);
            } else {
                that.isUpdatingProjects = false;
                that.progress = 0;
            }
        })
    }
    
    exportProjects() {
        this.projectService.exportProjects('csv');
    }

}
