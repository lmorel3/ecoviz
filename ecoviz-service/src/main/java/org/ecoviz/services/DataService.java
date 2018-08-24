/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.services;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ecoviz.domain.TreeData;
import org.ecoviz.domain.enums.ETreeDataType;
import org.ecoviz.services.providers.CountryChildrenProvider;
import org.ecoviz.services.providers.IDataProvider;
import org.ecoviz.services.providers.PartnersProvider;
import org.ecoviz.services.providers.ProjectsProvider;
import org.ecoviz.services.providers.TagChildrenProvider;
import org.ecoviz.services.providers.TagsProvider;

@ApplicationScoped
public class DataService {
    
    // TODO: Switch to a map of instances
    // Key = a ETreeDataType, Value = the corresponding provider

    @Inject
    private ProjectsProvider projectsProvider;
    
    @Inject
    private PartnersProvider partnersProvider;
    
    @Inject
    private TagsProvider tagsProvider;
    
    @Inject
    private TagChildrenProvider tagChildrenProvider;

    @Inject
    private CountryChildrenProvider countryChildrenProvider;
    
    /**
     * Fetches parent's children depending on parent's type
     * Parent's type is determined via "parentId". If it doesn't start 
     * by "ecoviz:", we consider it's a "PARTNER".
     */
    public List<TreeData> getChildren(String parentId) {               
        return getProvider(parentId).get(parentId);
    }
    
    /**
     * Gives parent's provider depending on a given type
     */
    public IDataProvider getProvider(String parentId) {
        IDataProvider provider;
        
        switch (ETreeDataType.get(parentId)) {
            case ROOT: provider = projectsProvider; break;
            case PROJECT: provider = partnersProvider; break;
            case PARTNER: provider = tagsProvider; break;
            case TAG: provider = tagChildrenProvider; break;
            case COUNTRY: provider = countryChildrenProvider; break;
            
            default: provider = projectsProvider; break;
        }
        
        return provider;
    }

}
