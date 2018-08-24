/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import { Component, OnInit, OnChanges, ViewChild, ElementRef, Input, ViewEncapsulation, Output, EventEmitter } from '@angular/core';
import * as d3 from 'd3';

import { GraphData } from '../../models/graph-data.model';
import { TreeComponent } from './tree.component';
import { TreeData } from '../../models/tree-data.model';
import { Location } from '../../models/location';
import { DataService } from '../../services/data-service';

import * as nominatim from 'nominatim-geocoder';
import { Observable, Subject, of, concat, from } from 'rxjs';
import { debounceTime, distinctUntilChanged, switchMap, tap, catchError, map, mergeAll, filter } from 'rxjs/operators';

@Component({
    selector: 'address-lookup',
    templateUrl: './address-lookup.component.html',
    styles: [`
        
    `],
    encapsulation: ViewEncapsulation.None
})
export class AddressLookupComponent implements OnInit, OnChanges {
    
    @Output() onSelected: EventEmitter<Location> = new EventEmitter();

    itemsLoader$: Observable<any>;
    isLoading = false;
    input$ = new Subject<string>();

    @Input()
    current: Location;

    selected: any;

    geocoder = new nominatim.Nominatim({secure: true}, {limit:10});

    constructor() { 

    }

    ngOnInit() {        
        this.initLoader()
    }

    ngOnChanges() {
        if(!!this.current) {
            this.selected = Location.toNominatim(this.current)
        }
    }
   
    initLoader() {
        this.itemsLoader$ = concat(
            of([]), // default items
            this.input$.pipe(
                debounceTime(300),
                distinctUntilChanged(),
                tap(() => this.isLoading = true),
                switchMap(term => this.search(term).pipe(
                    map(result => result.filter(a => a.osm_type === "way")), // Keep only "ways"
                    catchError(() => of([])), // empty list on error
                    tap(() => this.isLoading = false)
                ))
            )
        );   
    }

    search(term) {
        console.log(term);
        let promise: Promise<any> = this.geocoder.search({ q: term, addressdetails:true  });
        return from(promise)
    }

    onItemChange() {
        let location = Location.fromNominatim(this.selected);
        this.onSelected.emit(location);
    }

}
