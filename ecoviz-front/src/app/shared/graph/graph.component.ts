/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import { Component, OnInit, OnChanges, ViewChild, ElementRef, Input, ViewEncapsulation } from '@angular/core';
import * as d3 from 'd3';

import { GraphData } from '../../models/graph-data.model';
import { TreeComponent } from './tree.component';
import { TreeData } from '../../models/tree-data.model';
import { DataService } from '../../services/data-service';

@Component({
    selector: 'graph-project',
    templateUrl: './graph.component.html',
    styles: [],
    encapsulation: ViewEncapsulation.None
})
export class GraphComponent implements OnInit {
    
    @ViewChild('chart') private tree: TreeComponent;
    @Input() private data: GraphData;

    private dataService: DataService;

    private width: number;
    private height: number;
    private margin: any = {top: 20, right: 10, bottom: 60, left: 30};
  
    constructor(dataService: DataService) { 
        var w = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
        var h = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
        
        this.width = w - this.margin.left - this.margin.right;
        this.height = h - this.margin.top - this.margin.top;        

        this.dataService = dataService;
    }

    ngOnInit() {
        this.tree.setParams(this.width, this.height, this.margin);
        this.createGraph();
    }

    /**
     * Creates an empty graph, then fill it with 
     * projects
     */
    createGraph() {
        this.dataService.getChildren('ecoviz:root').then((children: TreeData[]) => {
            let root: TreeData = new TreeData('ecoviz:root', '', 1, 'ROOT');
            root.children = children;

            this.tree.run(root);
        }).catch(e => {
            alert("Unable to fetch data from API");
            console.error(e);  
        });

    }
    
}
