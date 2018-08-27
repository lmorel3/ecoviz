/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import { Component, OnInit } from '@angular/core';
import { SimpleModalComponent } from "ngx-simple-modal";

import { Organization } from '../../../models/organization';
import { OrganizationService } from '../../../services/organization-service';

export interface OrganizationModel {
    id: string;
    name: string;
}
@Component({
    selector: 'confirm',
    templateUrl: './link.component.html'
})
export class LinkComponent extends SimpleModalComponent<OrganizationModel, Organization> implements OrganizationModel, OnInit {
    
    id: string;
    name: string;

    availablePartners: Organization[] = [];

    selectedPartner: Organization;

    constructor(
        private partnerService: OrganizationService    ) {
        super();
    }

    ngOnInit(): void {
        this.loadAvailablePartners();
    }

    onSelected(event: any) {
        console.log(event);
        this.selectedPartner = event;
    }

    /**
     * Closes the modal and "returns" tags
     * of this partner
     */
    confirm() {
        // this.result is the returned value
        this.result = this.selectedPartner;
        this.close();
    }

    /**
     * Load available tags 
     */
    loadAvailablePartners(): void {
        this.partnerService.getPartners().subscribe((partners: Organization[]) => this.availablePartners = partners);
    };

}
