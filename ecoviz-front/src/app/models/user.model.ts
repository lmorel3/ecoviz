/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
export class User {
    id: string;
    username: string;
    password: string;
    roles: string[];

    constructor(username?: string, password?: string){
        this.username = username;
        this.password = password;
    }

}
