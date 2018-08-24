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

import { Observable } from 'rxjs/Observable';
import { of } from 'rxjs';
import { filter } from 'rxjs/operators';

import { Tag } from '../../../models/tag.model';
import { TagModel } from 'ngx-chips/core/accessor';
import { DataService } from '../../../services/data-service';
import { TreeData } from '../../../models/tree-data.model';
import { PartnerService } from '../../../services/partner-service';
import { TagService } from '../../../services/tag-service';
import { Partner } from '../../../models/partner.model';

export interface PartnerModel {
    id: string;
    name: string;
}
@Component({
    selector: 'confirm',
    templateUrl: './link.component.html'
})
export class LinkComponent extends SimpleModalComponent<PartnerModel, Partner> implements PartnerModel, OnInit {
    
    id: string;
    name: string;

    availablePartners: Partner[] = [];

    selectedPartner: Partner;

    constructor(
        private dataService: DataService,
        private partnerService: PartnerService,
        private tagService: TagService
    ) {
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
        this.partnerService.getPartners().subscribe((partners: Partner[]) => this.availablePartners = partners);
    };

}
