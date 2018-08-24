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

import { Member } from '../../models/member';

@Injectable()
export class MemberService {

    constructor(private http:HttpClient) {
    }

    getOrganizations() {
        return this.http.get(environment.apiUrl + '/api/members/organizations');
    }
    
    getMembers(): Promise<Member[]> {
        
        return new Promise<Member[]>((resolve, reject) => {
            
            this.getOrganizations().subscribe(
                (members: Array<any>) => {
                    let result = [];
                    
                    for(let m of members) {
                        result.push(Member.fromApi(m));
                    }
                    
                    resolve(result);
                },
                err => reject('Error while fetching data' + err)
            );
        });

    }

    importOrganizations(csvData: string) {
        return this.http.post<void>(environment.apiUrl + '/api/members/organizations/import', csvData);
    }
    
    deleteOrganization(member: Member) {
        return this.http.delete(environment.apiUrl + '/api/members/organizations/' + member.id);
    }

}
