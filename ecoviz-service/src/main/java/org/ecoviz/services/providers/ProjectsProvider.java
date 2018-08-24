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

import org.ecoviz.domain.Tag;
import org.ecoviz.domain.TreeData;
import org.ecoviz.domain.builders.TreeDataBuilder;
import org.ecoviz.domain.enums.ETreeDataType;
import org.ecoviz.repositories.TagRepository;

@ApplicationScoped
public class ProjectsProvider implements IDataProvider {
    
    @Inject
	TagRepository tagRepository;

    @Override
    public List<TreeData> get(String parentId) {
        List<TreeData> children = new LinkedList<>();
        
        for(Tag project : tagRepository.findByPrefix("ecoviz:project")) {
            
            TreeData data = new TreeDataBuilder()
                                    .id(project.getId())
                                    .name(project.getName())
                                    .childCnt(1L)
                                    .type(ETreeDataType.PROJECT)
                                    .build();
                                    
            children.add(data);             
            
        }
        return children;
    }

}
