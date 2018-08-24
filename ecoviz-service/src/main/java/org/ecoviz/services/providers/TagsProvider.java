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
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ecoviz.domain.Organization;
import org.ecoviz.domain.Tag;
import org.ecoviz.domain.TreeData;
import org.ecoviz.domain.builders.TreeDataBuilder;
import org.ecoviz.domain.enums.ETreeDataType;
import org.ecoviz.repositories.OrganizationRepository;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;

@ApplicationScoped
public class TagsProvider implements IDataProvider {

    @Inject	
    @Database(DatabaseType.DOCUMENT)
    OrganizationRepository organizationRepository;

    @Override
    public List<TreeData> get(String parentId) {
        List<TreeData> children = new LinkedList<>();
        
        Optional<Organization> optPartner = organizationRepository.findById(parentId);
        
        if(optPartner.isPresent()) {
            Organization partner = optPartner.get();

            // Append countries
            for(Tag tag : partner.getTagsByPrefix("ecoviz:country")) {
                children.add(new TreeDataBuilder()
                    .id(tag.getId())
                    .childCnt(1L)
                    .name(tag.getName())
                    .type(ETreeDataType.COUNTRY)
                    .build()
                );
            }

            // Append tags
            for(Tag tag : partner.getTagsByPrefix("ecoviz:tag")) {
                children.add(new TreeDataBuilder()
                    .id(tag.getId())
                    .childCnt(1L)
                    .name(tag.getName())
                    .type(ETreeDataType.TAG)
                    .build()
                );
            }

            // Append projects
            for(Tag tag : partner.getTagsByPrefix("ecoviz:project")) {
                children.add(new TreeDataBuilder()
                    .id(tag.getId())
                    .childCnt(1L)
                    .name(tag.getName())
                    .type(ETreeDataType.PROJECT)
                    .build()
                );
            }
            
        }
        
        return children;
    }
    
}
