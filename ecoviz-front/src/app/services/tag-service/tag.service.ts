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
import { filter, map, flatMap, tap } from 'rxjs/operators';

@Injectable()
export class TagService {

    constructor(private http:HttpClient) {
    }

    getUserTags(): Observable<any> {
        return this.http.get(environment.apiUrl + '/api/tags/ecoviz:tag');
    }

    getFilteredTags(prefixes: string[]): Observable<any> {
        return this.http.get(environment.apiUrl + '/api/tags').pipe(
            map((tags: Tag[]) => tags.filter((t) => TagService.filterTags(t, prefixes))),
            tap((tags: Tag[]) => tags.sort((a, b) => {
                if (a.id < b.id)
                    return -1;
                if (a.id > b.id)
                    return 1;
                return 0;
            }))
        )
    }

    static filterTags(tag: Tag, prefixes: string[]) {
        for(let p of prefixes) {
            if(tag.id.startsWith(p))
                return true;
        }
        return false;
    }

    getTags(): Observable<any> {
        return this.http.get(environment.apiUrl + '/api/tags');
    }

}
