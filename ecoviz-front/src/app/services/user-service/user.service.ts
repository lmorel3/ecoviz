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
import { User } from '../../models/user.model';

@Injectable()
export class UserService {

    constructor(private http:HttpClient) {
    }

    createUser(user: User) {
        return this.http.post(environment.apiUrl + '/api/users', user);
    }

    getUsers() {
        return this.http.get(environment.apiUrl + '/api/users');
    }

    deleteUser(id: string) {
        return this.http.delete(environment.apiUrl + '/api/users/' + id);
    }

}
