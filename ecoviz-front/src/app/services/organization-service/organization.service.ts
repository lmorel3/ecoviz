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
import { HttpClient } from '@angular/common/http';
import { environment } from 'environments/environment';

import { Organization } from '../../models/organization';
import { Tag } from '../../models/tag.model';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable()
export class OrganizationService {

    constructor(private http:HttpClient) {
    }

    _getOrganizations() {
        return this.http.get(environment.apiUrl + '/api/organizations');
    }
    
    getOrganizations (): Promise<Organization[]> {
        
        return new Promise<Organization[]>((resolve, reject) => {
            
            this._getOrganizations().subscribe(
                (members: Array<any>) => {
                    let result = [];
                    
                    for(let m of members) {
                        result.push(Organization.fromApi(m));
                    }
                    
                    resolve(result);
                },
                err => reject('Error while fetching data' + err)
            );
        });

    }

    importOrganizations(csvData: string) {
        return this.http.post<void>(environment.apiUrl + '/api/organizations/import', csvData);
    }

    merge(organizationId: string, partner: any) {
        return this.http.post(environment.apiUrl + '/api/organizations/' + organizationId + '/merge', partner);
    }

    split(organizationId: string) {
        return this.http.post(environment.apiUrl + '/api/organizations/' + organizationId + '/split', {});
    }
    
    deleteOrganization(member: Organization) {
        return this.http.delete(environment.apiUrl + '/api/organizations/' + member.id);
    }

    setTags(partnerId: string, tags: Tag[]): Observable<Object> {
        console.log('Updating tags of ' + partnerId, tags);
        return this.http.put(environment.apiUrl + '/api/organizations/' + partnerId + '/tags', tags);
    }
    
    getPartners(): Observable<Object> {
        return this.http.get(environment.apiUrl + '/api/organizations');
    }

    getPartnerById(id: String): Observable<Organization> {
        return this.http.get(environment.apiUrl + '/api/organizations/' + id).pipe(
            map((res: Organization) => res)
        );
    }

    updatePartner(partner: Organization): Observable<Object> {
        return this.http.put(environment.apiUrl + '/api/organizations/' + partner.id, partner);
    }
    

}
