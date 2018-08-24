/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.domain.dto;

import java.util.LinkedList;
import java.util.List;

public class OrganizationDto {
    
    private String name;
    private String memberType;
        
    private String id;
    private List<AddressDto> locations = new LinkedList<>();
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setLocations(List<AddressDto> locations) {
        this.locations = locations;
    }
    
    public List<AddressDto> getLocations() {
        return locations;
    }
    
    public void addLocation(AddressDto location) {
        locations.add(location);
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }
    
    public String getMemberType() {
        return memberType;
    }
    
    @Override
    public String toString() {
        return "Organization #" + id + " : " + name + " [" + memberType + "] @ " + getLocations();
    }
    
}
