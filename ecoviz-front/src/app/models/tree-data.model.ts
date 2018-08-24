/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
export class TreeData {

    public id: string;
    public name: string;
    public childCnt: number;
    public children: TreeData[];
    public type: string;

    constructor(id: string, name:string, childCnt: number, type: string) {
        this.id = id;
        this.name = name;
        this.childCnt = childCnt;
        this.children = [];
        this.type = type;
    }
}
