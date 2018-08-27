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

import { Location } from '../../../models/location';
import { Organization } from '../../../models/organization';
import { OrganizationService } from '../../../services/organization-service';
import { Tag } from '../../../models/tag.model';

export interface EditModalModel {
    id: String;
}
@Component({
    selector: 'confirm',
    templateUrl: './edit-modal.component.html'
})
export class EditModalComponent extends SimpleModalComponent<EditModalModel, Organization> implements EditModalModel, OnInit {
    
    currentLocation: Location;
    id: String;

    current: Organization;

    constructor(
        private partnerService: OrganizationService,
    ) {
        super();
    }

    ngOnInit(): void {
        this.partnerService.getPartnerById(this.id).subscribe((p: Organization) => {
            this.current = p;
            if(p.locations.length > 0) {
                this.currentLocation = p.locations[0];
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
            this.current.locations[0] = this.currentLocation;

            // Sets country code tag
            let idxCountryCode = this.current.tags.findIndex((t) => t.id.startsWith('ecoviz:country'))
            let countryCode = new Tag('ecoviz:country:' + this.currentLocation.countryCode.toLocaleLowerCase(), this.currentLocation.countryCode);

            if(idxCountryCode > -1) this.current.tags[idxCountryCode] = countryCode;
            else this.current.tags.push(countryCode);

            // Updates partner
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
