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

import { Tag } from '../../models/tag.model';
import { TagModel } from 'ngx-chips/core/accessor';
import { DataService } from '../../services/data-service';
import { TreeData } from '../../models/tree-data.model';
import { PartnerService } from '../../services/partner-service';
import { TagService } from '../../services/tag-service';

export interface TagModalModel {
    id: string;
    name: string;
}
@Component({
    selector: 'confirm',
    templateUrl: './tags-modal.component.html'
})
export class TagsModalComponent extends SimpleModalComponent<TagModalModel, Tag[]> implements TagModalModel, OnInit {
    
    id: string;
    name: string;

    tags: Tag[] = [];
    availableTags: Tag[] = [];

    constructor(
        private dataService: DataService,
        private partnerService: PartnerService,
        private tagService: TagService
    ) {
        super();
    }

    ngOnInit(): void {
        this.loadPartnerTags();
        this.loadAvailableTags();
    }

    /**
     * Closes the modal and "returns" tags
     * of this partner
     */
    confirm() {
        // this.result is the returned value
        this.result = this.tags;
        
        // Updates tags list
        let that = this;
        this.partnerService.setTags(this.id, this.result).subscribe(() => {
            that.close();
        });
    }

    /**
     * Load available tags 
     */
    loadAvailableTags(): void {
        this.tagService.getUserTags().subscribe(tags => this.availableTags = tags);
    };

    /**
     * Load partner tags
     */
    loadPartnerTags(): void {
        this.dataService.getChildren(this.id).then((data: TreeData[]) => {
            let tags = [];
            for(let d of data.filter(t => t.id.startsWith('ecoviz:tag'))) {
                tags.push(new Tag(d.id, d.name));
            }
            this.tags = tags;
        });
    }


}
