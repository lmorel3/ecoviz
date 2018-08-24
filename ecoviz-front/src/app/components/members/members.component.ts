/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
import {
  Component,
  OnInit,
  ViewChild, ElementRef
} from '@angular/core';

import { icon, latLng, marker, point, polyline, tileLayer, MarkerClusterGroup, Marker, divIcon, Icon, control, Control } from 'leaflet';
import 'leaflet.markercluster';
import 'leaflet-sidebar-v2'

import { MemberService } from '../../services/member-service';
import { Member, EMemberType } from '../../models/member';
import L from 'leaflet';

import { FormGroup, FormBuilder } from '@angular/forms';
import { PartnerService } from '../../services/partner-service';
import { SimpleModalService } from 'ngx-simple-modal';
import { LinkComponent } from './link/link.component';
import { Partner } from '../../models/partner.model';
import { AuthService } from '../../services/auth-service';
import { NgSelectComponent } from '@ng-select/ng-select';
import { Tag } from '../../models/tag.model';
import { TagService } from '../../services/tag-service';
import { TagsModalComponent } from '../../shared/tags-modal';


@Component({
  selector: 'members-map',
  providers: [
  ],
  styleUrls: [ './members.component.scss' ],
  templateUrl: './members.component.html'
})
export class MembersComponent implements OnInit {
  
    @ViewChild('select')
    private select: NgSelectComponent;

    private layers: any[] = [];
    private options: any = {};
    private layerControls: any[] = [];

    private clusterOptions: any = {};
    private clusterData: any[] = [];
    private clusterGroup: MarkerClusterGroup; 

    private sidebar: Control.Sidebar;

    private keywords: string = "";

    private availableTags: Tag[];
    private toggledItems: string[] = ['partners', 'members', 'untagged'];

    myForm: FormGroup;
    members: Member[] = [];
    map: L.Map;

    selectedItem: any;
    memberType = EMemberType.ORGANIZATION;
    organizationAndPartnerType = EMemberType.ORGANIZATION_PARTNER;

   /**
    * TypeScript public modifiers
    */
    constructor(
        private memberService: MemberService,
        private partnerService: PartnerService,
        private formBuilder: FormBuilder,
        private simpleModalService: SimpleModalService,
        private tagService: TagService,
        private authService: AuthService
    ) {}

    public ngOnInit() {

        this.clusterData = [];

        this.options = {
            layers: [
                tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 18, attribution: '...' })
            ],
            zoomControl: false
        };

        this.myForm = this.formBuilder.group({
            keyword: ''
        });
          
    }

    onMapReady(map: L.Map) {
        this.map = map;
        this.sidebar = control.sidebar({ container: 'sidebar', position: 'left' }).addTo(map);

        map.on('zoomend', () => {
            var zoomLevel = map.getZoom();
            let tooltipContainer: HTMLElement = document.querySelector('.leaflet-tooltip-pane');
            tooltipContainer.style.visibility = (zoomLevel >= 8) ? 'visible' : 'hidden';
        });

        map.setView([30,0], 2);
    }

    initMap(clusterGroup: MarkerClusterGroup) {

        // Displayed tags in sidemenu
        let tagsPrefix = ['ecoviz:tag', 'ecoviz:membership', 'ecoviz:project'];
        this.tagService.getFilteredTags(tagsPrefix).subscribe((tags) => {
            this.availableTags = tags;

            for(let tag of tags) {
                this.toggledItems.push(tag.id);
            }
         
            this.fetchMembers(clusterGroup);
        });

    }

    onSearch(member: Member) {
        this.selectedItem = member;
        
        if(!!member && member.locations.length > 0) {
            let address = member.locations[0];
            this.centerMap(address, 12);
        } 
    }

    centerMap(address: any, zoom: number = 8) {
        this.map.setView(latLng(address.latitude, address.longitude), zoom);
    }

    /**
     * Fetches members and partners. Then, displays it on the map
     */
    private fetchMembers(clusterGroup: MarkerClusterGroup) {
        this.clusterGroup = clusterGroup;

        /** 
         *  Filters members : 
         *    - Remove partners linked to a member
         *    - Set specific type 'ORGANIZATION_PARTNER' for members linked to a partner
         **/
        this.memberService.getMembers().then((members: Member[]) => {
            this.members = members;
            this.displayIcons(this.members, clusterGroup);
        });
        
    }
    
    private displayIcons(members: Member[], clusterGroup: MarkerClusterGroup) {
        this.clusterGroup.clearLayers();
        let icons = this.getIcons();

        let customMarker = Marker.extend({
            options: {
                data: {}
            }
        });
        
        for(let member of members) {
            for(let address of member.locations) {
                
                if(this.shouldDisplayIcon(member)) {
                    let marker = new customMarker(
                        latLng(address.latitude, address.longitude),
                        {
                            data: member
                        }
                    ).bindTooltip(member.name, { permanent: true });
    
                    marker.setIcon(icons[member.type]);
    
                    clusterGroup.addLayer(marker);
                }
                
            }
        }

        let that = this;

        clusterGroup.on('click', (e: any) => {
            this.selectedItem = e.layer.options.data;
            this.keywords = this.selectedItem.name;

            this.openSidebar('home');
        });

        clusterGroup.on('clusterclick', (e) => {
            
        });
        
    }
    
    onClick(eventType: string, event) {
        this.sidebar.close();        
    }

    focusSearch() {
        let sidebarPane: HTMLElement = document.querySelector('.leaflet-sidebar-pane') as HTMLElement;
        sidebarPane.classList.remove('active');
            
        let elem: HTMLElement = document.querySelector('.ng-input > input');
        elem.focus();
    }

    /**
     * Opens a modal popup for linking a Partner with a member
     */
    showLinkModal() {
        let that = this;

        let member = this.selectedItem;
        this.simpleModalService.addModal(LinkComponent, member)
            .subscribe((partner) => {
                /** Links member and partner, then reloads the map **/
                if(!!partner) {
                    partner.memberId = member.id;
                    that.partnerService.updatePartner(partner).subscribe(() => {
                        that.sidebar.close()
                        that.reloadMap()
                    });
                }
            });
    }

    showUnlinkPopup() {
        if(confirm('Are you sure?')) {
            let that = this;
            let memberId = that.selectedItem.id;
            this.partnerService.getPartners().subscribe((partners: Partner[]) => {
                let partner = partners.find(item => item.memberId === memberId);

                /** Once the corresponding partner as been found, the link is deleted, and map updated **/
                if(!!partner) {
                    partner.memberId = null;
                    that.partnerService.updatePartner(partner).subscribe(() => {
                        that.sidebar.close()
                        that.reloadMap()
                    });
                }

            });
        }
    }

    showTagsModal() {
        let member = this.selectedItem;

        let that = this;
        this.simpleModalService.addModal(TagsModalComponent, {
            id:   member.id,
            name: member.name
        }).subscribe((tags) => {
            that.sidebar.close()
            that.reloadMap()
        });
    }

    deleteMember() {
        if(confirm('Are you sure?')) {
            let member = this.selectedItem; let that = this;
            this.memberService.deleteOrganization(member).subscribe((tags) => {
                that.sidebar.close()
                that.reloadMap()
            });
        }
    }

    reloadMap() {
        this.selectedItem = null;
        this.fetchMembers(this.clusterGroup);
    }

    openSidebar(tab: string) {
        // Workaround to refresh view... :/
        let sidebarPane: HTMLElement = document.querySelector('.leaflet-sidebar-pane') as HTMLElement;
        sidebarPane.classList.remove('active');
            
        let sideContent: HTMLElement = document.querySelector('ng-select') as HTMLElement;
        sideContent.click();

        this.sidebar.open(tab);
    }

    private getIcons() {
        let icons = {};

        icons[EMemberType.PARTNER] = new Icon({
            iconUrl: 'https://cdn.rawgit.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowSize: [41, 41]
        });
        icons[EMemberType.ORGANIZATION] = new Icon({
            iconUrl: 'https://cdn.rawgit.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-orange.png',
            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowSize: [41, 41]
        });
        icons[EMemberType.ORGANIZATION_PARTNER] = new Icon({
            iconUrl: 'https://cdn.rawgit.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-blue.png',
            shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
            iconSize: [25, 41],
            iconAnchor: [12, 41],
            popupAnchor: [1, -34],
            shadowSize: [41, 41]
        });

        return icons;
    }

    /**
     * Toggle markes
     */
    toggle(item: string) {
        if(this.isVisible(item)) {
            this.toggledItems.splice(this.toggledItems.indexOf(item), 1);
        } else {
            this.toggledItems.push(item);
        }

        this.displayIcons(this.members, this.clusterGroup)
    }

    isVisible(item: string) {
        return this.toggledItems.indexOf(item) > -1; 
    }

    shouldDisplayIcon(member: Member) {
    
        if(member.isOrganization() && !this.isVisible('members')) {
            return false;
        }

        if(member.isPartner() && !this.isVisible('partners')) {
            return false;
        }

        if(this.isVisible('untagged') && member.getUserTags().length === 0) {
            return true;
        }
    
        // If at least one tag is toggled: ok
        for(let tag of member.tags) {
            if(this.isVisible(tag.id)) {
                return true;
            }
        }

        return false;

    }

    selectAllTags() {
        this.unselectAllTags();
        for(let tag of this.availableTags) {
            this.toggledItems.push(tag.id);
        }
        this.toggledItems.push('untagged')
        this.displayIcons(this.members, this.clusterGroup)
    }

    unselectAllTags() {
        this.toggledItems = this.toggledItems.filter((tagId) => tagId === 'members' || tagId === 'partners')
        this.displayIcons(this.members, this.clusterGroup)
    }

    getTagStyle(tag: Tag): string {

        if(tag.id.startsWith('ecoviz:membership'))
            return 'is-success';
    
        if(tag.id.startsWith('ecoviz:project'))
            return 'is-danger';
    
        if(tag.id.startsWith('ecoviz:tag'))
            return 'is-info';
    
    }

}
