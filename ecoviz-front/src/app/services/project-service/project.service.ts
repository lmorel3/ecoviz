/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ResponseContentType } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { environment } from 'environments/environment';
import { saveAs } from 'file-saver';
import { map } from 'rxjs/operators';

@Injectable()
export class ProjectService {

    constructor(private http:HttpClient) {
    }

    getProjects() {
        console.log('Calling ' + environment.apiUrl + '/api/projects');
        return this.http.get(environment.apiUrl + '/api/projects');
    }
    
    getImportProgress(id: string) {
        return this.http.get(environment.apiUrl + '/api/projects/import/' + id + '/progress');
    }

    importProjects(csv: string) {
        return this.http.post(environment.apiUrl + '/api/projects/import', csv);
    }

    exportProjects(type: string) {
        return this.downloadResource(environment.apiUrl + '/api/projects/export/' + type);
    }

    public async downloadResource(url: string) {
        return this.http.get(url, { responseType: 'blob' })
        .subscribe(
            response => { 
                console.log(response);
                var blob = new Blob([response], {type: 'text/csv'});
                var filename = 'projects.csv';
                saveAs(blob, filename);
            },
            error => {
                console.error(`Error: ${error.message}`);
            }
        );
    }

}
