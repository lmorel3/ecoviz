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
import { Location } from '../../../models/location';

export interface EditModalModel {
    id: String;
}
@Component({
    selector: 'confirm',
    templateUrl: './edit-modal.component.html'
})
export class EditModalComponent extends SimpleModalComponent<EditModalModel, Partner> implements EditModalModel, OnInit {
    
    currentLocation: Location;
    id: String;

    current: Partner;

    constructor(
        private partnerService: PartnerService,
    ) {
        super();
    }

    ngOnInit(): void {
        this.partnerService.getPartnerById(this.id).subscribe((p: Partner) => {
            this.current = p;
            if(!!p.location) {
                p.location.countryCode = p.country;
                this.currentLocation = p.location;
            }
        });
    }

    /**
     * Closes the modal and "returns" tags
     * of this partner
     */
    confirm() {
        // this.result is the returned value

        // Updates tags list
        let that = this;

        // If an address has been set
        if(!!this.currentLocation) {
            this.current.country = this.currentLocation.countryCode;
            this.current.location = this.currentLocation;

            this.result = this.current;
            this.partnerService.updatePartner(this.current).subscribe(() => {
                that.close();
            });
        } else {
            this.result = this.current;
            that.close();
        }
    }

    
    onAddressSelected(location) {
        this.currentLocation = location;
    }


}
