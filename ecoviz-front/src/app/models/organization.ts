/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import { Tag } from './tag.model';

export enum EMemberType {
    ORGANIZATION,
    PARTNER,
    ORGANIZATION_PARTNER // Both member & project partner
}

export class Organization {
    
    id: String;
    name: String;
    locations: Array<any>;
    
    type: EMemberType;

    memberId: String; // temporary
    tags: Tag[] = [];

    isOrganization() {
        return this.type === EMemberType.ORGANIZATION || this.type === EMemberType.ORGANIZATION_PARTNER;
    }

    isPartner() {
        return this.type === EMemberType.PARTNER || this.type === EMemberType.ORGANIZATION_PARTNER;
    }

    getUserTags() {
        return this.tags.filter((t) => t.id.startsWith('ecoviz:tag'));
    }
    
    static fromApi(data: any): Organization{
        let member = new Organization();
        
        member.id        = data.id;
        member.name      = data.name;
        member.locations = data.locations;
        
        member.type      = Organization.getType(data.tags);
        member.tags      = data.tags;

        return member;
    }

    private static getType(tags): EMemberType {
        let isPartner = tags.filter((t) => t.id.startsWith('ecoviz:project')).length > 0
        let isMember = tags.filter((t) => t.id.startsWith('ecoviz:membership') && t.id !== 'ecoviz:membership:p').length > 0
        
        if(isPartner && isMember)
            return EMemberType.ORGANIZATION_PARTNER;
        else if(isPartner)
            return EMemberType.PARTNER;

        return EMemberType.ORGANIZATION;
    }

}
