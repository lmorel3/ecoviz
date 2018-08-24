/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import * as moment from "moment";

import { environment } from 'environments/environment';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

import { tap, share } from "rxjs/operators";
import { of, Observable } from "rxjs";

@Injectable()
export class AuthService {

    static jwtHelper = new JwtHelperService();

    constructor(private http: HttpClient) {

    }

    login(username:string, password:string ) {
        return this.http.post<any>(environment.apiUrl + '/api/users/login', {username: username, password: password})
            .pipe(tap(this.setSession), share()); 
    }
          
    private setSession(authResult) {
        const expiresAt = moment().add(authResult.expiresIn, 'seconds');

        localStorage.setItem('token', authResult.token);
        localStorage.setItem("expires_at", JSON.stringify(expiresAt.valueOf()) );
    }          

    logout() {
        localStorage.removeItem("token");
        localStorage.removeItem("expires_at");
    }

    public isLogged() {
        if(!localStorage.getItem("expires_at")) {
            return false;
        }

        return moment().isBefore(this.getExpiration());
    }

    public isAdmin() {
        let token = this.decodeToken();
        return token.groups.includes('admin') && this.isLogged();
    }

    private decodeToken() {
        let token = localStorage.getItem("token");

        // If not logged in
        if(!token) { return { groups: [] }; }

        return AuthService.jwtHelper.decodeToken(token);
    }

    getToken() {
        return localStorage.getItem('token')
    }

    getExpiration() {
        const expiration = localStorage.getItem("expires_at");
        const expiresAt = JSON.parse(expiration);
        return moment(expiresAt);
    }    
}
          
