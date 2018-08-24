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
import { Observable } from 'rxjs/Observable';
import { environment } from 'environments/environment';

import { Member, EMemberType } from '../../models/member';
import { Location } from '../../models/location';
import { TreeData } from '../../models/tree-data.model';

@Injectable()
export class DataService {

    constructor(private http:HttpClient) {
    }

    private _getChildren(parentId: string) {
        return this.http.get(environment.apiUrl + '/api/data/children/' + parentId);
    }
    
    getChildren(parentId: string): Promise<TreeData[]> {
        return new Promise<TreeData[]>((resolve, reject) => {
            this._getChildren(parentId)
                .subscribe((children: TreeData[]) => {
                    resolve(children);
                },
                err => reject('Error while fetching data' + err)
            );
        });            
        
    }
    

}
