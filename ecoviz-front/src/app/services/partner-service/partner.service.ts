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
import { Tag } from '../../models/tag.model';
import { Partner } from '../../models/partner.model';
import { Member } from '../../models/member';
import { map } from 'rxjs/operators';

@Injectable()
export class PartnerService {

    constructor(private http:HttpClient) {
    }

    setTags(partnerId: string, tags: Tag[]): Observable<Object> {
        console.log('Updating tags of ' + partnerId, tags);
        return this.http.put(environment.apiUrl + '/api/partners/' + partnerId + '/tags', tags);
    }
    
    getPartners(): Observable<Object> {
        return this.http.get(environment.apiUrl + '/api/partners');
    }

    getPartnerById(id: String): Observable<Partner> {
        return this.http.get(environment.apiUrl + '/api/partners/' + id).pipe(
            map((res: Partner) => res)
        );
    }

    updatePartner(partner: Partner): Observable<Object> {
        return this.http.put(environment.apiUrl + '/api/partners/' + partner.id, partner);
    }
    
}
