/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import * as d3 from 'd3';
import { Component, OnInit, OnChanges, ViewChild, ElementRef, Input, ViewEncapsulation } from '@angular/core';
import { contextmenu } from '@atago0129/d3-v4-contextmenu'
import { TreeData } from '../../models/tree-data.model';
import { DataService } from '../../services/data-service';
import { TagsModalComponent } from '../tags-modal';
import { SimpleModalService } from 'ngx-simple-modal';
import { AuthService } from '../../services/auth-service';

import * as d3_utils from './d3.utils';
import { EditModalComponent } from '../../components/home/edit-modal/edit-modal.component';

@Component({
    selector: 'tree',
    template: '<div id="tree-container" #chartContainer></div>',
    styleUrls: [ './tree.component.scss' ],
    encapsulation: ViewEncapsulation.None
})
export class TreeComponent implements OnInit {

    @ViewChild('chartContainer') private container: ElementRef;

    ngOnInit(): void {
        console.debug("TreeComponent loaded");
    }

    constructor(private dataService: DataService,
                private simpleModalService: SimpleModalService,
                private authService: AuthService) {
    }

    private width: number;
    private height: number;
    private margin: any;

    private duration: number = 200;

    /**
     * Tree attributes
     */
    private treemap: any;
    private root: any;
    private svg: any;

    private labelTooltip: any;

    private lastClickedNode: any;

    setParams(width: number,
               height: number,
               margin: any) {

        this.width   = width;
        this.height  = height;
        this.margin  = margin;  
        
        this.labelTooltip = d3.select("body").append("div")
            .attr("id", "label_tooltip")
            .style("opacity", 0);
    }

    /**
     * Get children of a node, and fetches data
     * from the API if necessary
     * @param currentNode
     */
    getData(currentNode: any): Promise<any> {
        return new Promise<TreeData[]>((resolve, reject) => {
            this.dataService.getChildren(currentNode.data.id)
                .then((children: TreeData[]) => {
            

                //Selected is a node, to which we are adding the new node as a child
                //If no child array, create an empty array
                if(!currentNode._children){
                    currentNode._children = [];
                    currentNode.data.children = [];
                }
                
                //Creates a Node from newNode object using d3.hierarchy(.)
                for(let child of children) {
                    let newNode: any = d3.hierarchy(child);

                    newNode.depth = currentNode.depth + 1; 
                    newNode.height = currentNode.height - 1;
                    newNode.parent = currentNode; 
                    newNode.id = Date.now();
                    newNode.data = child;

                    //Push it to parent.children array  
                    currentNode._children.push(newNode);
                    currentNode.data.children.push(newNode.data);

                }
                
                resolve(currentNode);
            })
        });            
    }
    
    /**
     * Creates a new tree with the provided data
     */
    run(rootData: TreeData){
        
        // Defines dimensions
        let margin = this.margin;
        let height = this.height - margin.top - margin.bottom;
        let width = this.width - margin.left - margin.right;
        
        //Append the svg object to the container
        let svg = d3.select(this.container.nativeElement)
            .append('svg')
            .attr('width', width + margin.left + margin.right)
            .attr('height', height + margin.top + margin.bottom);
            
        svg.append('g')
            .attr('transform', 'translate(' + margin.left + ',' + margin.top + ')');

        svg.call(d3.zoom().on("zoom", function () {
            svg.attr("transform", d3.event.transform)
        }))

        this.appendFilters(svg);

        let duration = this.duration;
        function move(moveX, moveY) {   
            let transform = svg.attr('transform').replace(/[^0-9\-.,]/g, '').split(',');
            let newX = parseFloat(transform[0])+moveX;
            let newY = parseFloat(transform[1])+moveY;

            svg.transition().duration(duration).attr('transform', 'translate(' + newX + ',' + newY + ')')
        }

        d3.select('body').on('keydown', () => {

            switch(d3.event.keyCode) {
                case 37: move(-10, 0); break; // left
                case 38: move(0, 10); break; // up
                case 39: move(10, 0); break; // right
                case 40: move(0, -10); break; // down
            }

        });

        let resetBtn = d3.select(this.container.nativeElement)
            .append('button')
            .attr('class', 'button is-small')
            .attr('id', 'recenter-btn')
            .html('Recenter')
            .on('click', () => {
                if(!!this.lastClickedNode) this.centerNode(this.lastClickedNode);
            });

        this.svg = svg;

        // declares a tree layout and assigns the size of the tree
        this.treemap = d3.tree().size([height, width]);

        // assign parent, children, height, depth
        let root:any = d3.hierarchy(rootData, (d: TreeData) => { return d.children });
        root.x0 = height / 2; // left edge of the rectangle
        root.y0 = 0; // top edge of the triangle

        // collapse after the second level
        if(root.children) {
            root.children.forEach(this.collapse);
        }
        
        this.root = root;
        this.update(this.root);
    }

    // collapse the node and all it's children
    collapse(d) {
        if (d.children) {
            d._children = d.children;
            d._children.forEach(this.collapse);
            d.children = null;
        }
    }

    // toggle children on click
    click(d) {
        if(d.depth > 6)
            return alert('Tip: right click > set as root');

        let promise: Promise<any>;

        // If data has to be retrieved from API
        if(d.data.childCnt > 0 && !d.children && !d._children) {
            promise = this.getData(d);
        } else {
            promise = new Promise((resolve) => { resolve(d); });
        }

        // Refresh graph
        promise.then((d: any) => {
            if (d.children) {
                d._children = d.children;
                d.children = null;
            } else {
                d.children = d._children;
                d._children = null;
            }
            this.update(d);
        });

    }

    /**
     * Select a node as the root, and draw
     * a new tree from this node
     * @param d 
     */
    setAsRoot(d) {
        let that = this;
        d3.select(this.container.nativeElement).transition().selectAll('svg').remove();
        this.dataService.getChildren(d.data.id).then((children: TreeData[]) => {
            let data: TreeData = new TreeData(d.data.id, d.data.name, d.data.childCnt, d.data.type);
            data.children = children;

            that.run(data);
        });
    }
    
    /**
     * When data is loaded, this function is called
     * It's in charge of placing elements, adjusting size, 
     * adding labels, circles, etc.
     * @param source 
     */
    update(source) {
        this.lastClickedNode = source;

        let height = (this.svg.attr('height') * (source.depth+1));
        this.svg.attr('height', height);

        let that = this;
        let margin = this.margin;
        
        // assigns the x and y position for the nodes
        let treeData = this.treemap(this.root);

        // compute the new tree layout
        let nodes = treeData.descendants(),
            links = treeData.descendants().slice(1);

        // normalise for fixed depth
        nodes.forEach(function(d) { d.y = d.depth * 180; });

        // ****************** Nodes section ***************************

        // update the nodes ...
        let node = this.svg.selectAll('g.node')
            .data(nodes, function(d) { return d.id; });

        // Enter any new modes at the parent's previous position.
        let nodeEnter = node.enter().append('g')
            .attr('class', 'node')
            .attr('transform', function(d) {
                return 'translate(' + (source.y0 + margin.top) + ',' + (source.x0 + margin.left) + ')';
            })
            .on('click', (d) => { that.click(d); })
            
            .on("mouseover", function(d) {
                if(d.data.name.length > 20) {
                    that.labelTooltip.transition()
                    .duration(200)
                    .style("opacity", 1);
                    that.labelTooltip.text(d.data.name)
                    .style("left", (d3.event.pageX) + "px")
                    .style("top", (d3.event.pageY - 28) + "px");
                }
            })
            .on("mouseout", function(d) {
                that.labelTooltip.transition()
                    .duration(500)
                    .style("opacity", 0);
            });

        // add circle for the nodes
        nodeEnter.append('circle')
            .attr('class', 'node')
            .attr('r', 1e-6)
            
        // We append the context menu to partners
        nodeEnter.on('contextmenu', (d) => {
            this.getContextMenu(d)(d)
        });
        
        // add labels for the nodes
        nodeEnter.append('text')
            .attr('dy', '.35em')
            .attr('x', function(d) {
                if(d.depth === 0)
                    return 12;

                return d.children && !d.data._children ? -1.3 * d.data.name.length : 13;
            })
            .attr('y', function(d) {
                if(d.depth === 0)
                    return -0.5;
                    
                return d.children && !d.data._children ? margin.top : 0;
            })
            .attr('class', (d) => {
                if(d.depth === 0) return 'rootNode'
                else if(!!d.children) return 'hasChildren'
            })
            .attr('text-anchor', function(d) {
                if(d.depth === 0)
                    return 'start';

                return d.children && !d.data._children ? 'middle' : 'start';
            })
            .attr('filter', (d) => {
                if(d.depth === 0 || !!d.children)
                    return 'url(#textBackground)';
            }).text(function(d) {
                return d.data.name;
            }).call(this.wrap, 100)

        // add number of children to node circle
        nodeEnter.append('text')
            .attr('x', (d) => {
                return (d.data.childCnt >= 10) ? -5 : -3;
            })
            .attr('y', 4)
            .attr('cursor', 'pointer')
            .attr('fill', '#F7FFF7')
            .style('font-size', '10px')
            .style('font-weight', '600')
            .text((d) => {
                return (d.data.childCnt > 1) ? d.data.childCnt : '';
            });

        // UPDATE
        let nodeUpdate = nodeEnter.merge(node);

        // transition to the proper position for the node
        nodeUpdate.transition().duration(this.duration)
            .attr('transform', function(d) {
                return 'translate(' + (d.y + margin.top) + ',' + (d.x + margin.left) + ')';
            });

        // update the node attributes and style
        nodeUpdate.select('circle.node')
            .attr('r', (d) => {
                return (d.data.childCnt > 1) ? 10 : 6;
            })
            .attr('class', (d) => { return d.data.type; })
            .attr('cursor', 'pointer');

        // remove any exiting nodes
        let nodeExit = node.exit()
            .transition().duration(this.duration)
            .attr('transform', function(d) {
                return 'translate(' + (source.y + margin.top) + ',' + (source.x + margin.left) + ')';
            })
            .remove();

        // on exit reduce the node circles size to 0
        nodeExit.select('circle')
            .attr('r', 1e-6);

        // on exit reduce the opacity of text labels
        nodeExit.select('text')
            .style('fill-opacity', 1e-6);

        // ****************** links section ***************************

        // update the links
        let link = this.svg.selectAll('path.link')
            .data(links, function(d) { return d.id });

        // enter any new links at the parent's previous position
        let linkEnter = link.enter().insert('path', 'g')
            .attr('class', 'link')
            .attr('d', function(d) {
                let o = {x: source.x0 + margin.left, y: source.y0 + margin.top};
                return diagonal(o, o);
            });

        // UPDATE
        let linkUpdate = linkEnter.merge(link);

        linkUpdate.transition().duration(this.duration)
        .attr('d', function(d) { return diagonal(d, d.parent); });

        // remove any exiting links
        let linkExit = link.exit()
            .transition().duration(this.duration)
            .attr('d', function(d) {
                var o = {x: source.x, y: source.y};
                return diagonal(o, o);
            })
            .remove();

        // store the old positions for transition
        nodes.forEach((d:any) => {
            d.x0 = d.x + margin.left;
            d.y0 = d.y + margin.top;
        });

        // creates a curved (diagonal) path from parent to the child nodes
        function diagonal(s, d) {
            let path = 'M ' + (s.y + margin.top) + ' ' + (s.x + margin.left) +
                    'C ' + ((s.y + d.y + (margin.top * 2)) / 2) + ' ' + (s.x + margin.left) +
                    ', ' + ((s.y + d.y + (margin.top * 2)) / 2) + ' ' + (d.x + margin.left) +
                    ', ' + (d.y + margin.top) + ' ' + (d.x + margin.left);
            return path;
        }

        this.centerNode(source)
    }

    /**
     * Center the tree depending on the "source" node
     */
    centerNode(source) {

        let isRoot = source.depth === 0;
        let x = -source.y0;
        let y = -source.x0;

        // Center the parent if possible
        if(!!source.children) {
            let child = source.children[0]
            x = -child.y;
        }

        x = x * 1 + this.width / 2;
        y = y * 1.2 + this.height / 2;

        if(isRoot) {
            y = 0;
        }

        this.svg.transition()
            .duration(this.duration)
            .attr("transform", "translate(" + x + "," + y + ")");
    }

    /**
     * Creates a context menu for opening 
     * tags editor modal
     */
    private getContextMenu(node) {
        let that = this;
        let items: any[] = [
            {
              label: "Set as root",
              action: function (d, i) {
                that.setAsRoot(d);
              }
            }
        ];

        if(this.authService.isAdmin() && node.data.type === 'PARTNER') {
            items.push({
                label: "Edit tags",
                action: function (d, i) {
                    that.showTagsModal(d);
                }
            }, {
                label: "Edit address",
                action: function (d, i) {
                    that.showEditModal(d);
                }              
            });
        }

        return contextmenu(items);
    }

    /**
     * Opens a modal popup for editing tags 
     */
    showTagsModal(d: any) {
        this.simpleModalService.addModal(TagsModalComponent, {
              id: d.data.id,
              name: d.data.name
            })
            .subscribe(() => {
                this.resetNode(d);
            });
    }

    /**
     * Opens a modal popup for editing tags 
     */
    showEditModal(d: any) {
        this.simpleModalService.addModal(EditModalComponent, d.data)
            .subscribe((tags) => {
                this.resetNode(d);
            });
    }

    resetNode(d: any) {
        if(!d.children) return; // don't expand if already collapsed

        if(d._children == null) {
            this.collapse(d);
        }
        
        d.children = null;
        d._children = null;

        this.click(d); // Reload children
    }

    /**
     * Appends various SVG filter so that they are
     * available in this SVG document
     */
    private appendFilters(svg) {
        let filterDef = svg.append("defs");
        let filter = filterDef.append("filter")
                    .attr("id", "textBackground")
                    .attr("x", 0)
                    .attr("y", 0)
                    .attr("width", 1)
                    .attr("height", 1);
        filter         
                    .append("feFlood")
                    .attr("flood-color", "white")
        filter.append("feComposite")
                    .attr("in", "SourceGraphic");            
    }

    /**
     * Append "..." at the end of a label if necessary
     */
    private wrap(text, width) {
        text.each(function() {
        var text = d3.select(this),
            words = text.text().split(/\s+/).reverse(),
            word,
            line = [],
            lineNumber = 0,
            lineHeight = 1.1, // ems
            y = text.attr("y"),
            x = text.attr("x"),
            dy = parseFloat(text.attr("dy")),
            tspan = text.text(null).append("tspan").attr("x", x).attr("y", y).attr("dy", dy + "em"),
            stop = false;
            while ((word = words.pop()) && !stop) {
                line.push(word);
                tspan.text(line.join(" "));

                // @ts-ignore
                if (tspan.node().getComputedTextLength() > width) {
                    line.pop();
                    tspan.text(line.join(" "));
                    line = [word];
                    tspan = text.append("tspan").attr("x", x).attr("y", y).attr("dy", ++lineNumber * lineHeight + dy + "em").text(word);
                }

            }

            // Append "..." if text is too long
            if(lineNumber > 1) {
                // @ts-ignore
                let children = text.selectAll('tspan')._groups[0]
                
                let i:any = 0
                for(i in children) {
                    if(i>1) children[i].remove()
                }
                
                // @ts-ignore
                let last = text.selectAll('tspan')._groups[0][1]
                // @ts-ignore
                text.selectAll('tspan')._groups[0][1].innerHTML = last.innerHTML + "..."  
            }

        });
    }

}
