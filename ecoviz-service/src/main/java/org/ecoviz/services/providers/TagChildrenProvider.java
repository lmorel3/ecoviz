/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.services.providers;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ecoviz.domain.Organization;
import org.ecoviz.domain.TreeData;
import org.ecoviz.domain.builders.TreeDataBuilder;
import org.ecoviz.domain.enums.ETreeDataType;
import org.ecoviz.services.OrganizationService;

@ApplicationScoped
public class TagChildrenProvider implements IDataProvider {
    
    @Inject
    OrganizationService organizationService;
    
    @Override
    public List<TreeData> get(String parentId) {
        List<TreeData> children = new LinkedList<>();
        
        for(Organization organization : organizationService.findByTag(parentId)) {
            
            TreeData data = new TreeDataBuilder()
                                    .id(organization.getId())
                                    .name(organization.getName())
                                    .childCnt(1L)
                                    .type(ETreeDataType.PARTNER)
                                    .build();
                                    
            children.add(data);             
            
        }
        return children;
    }

}
