/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.repositories;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ecoviz.domain.Organization;
import org.ecoviz.domain.Tag;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.document.query.DocumentQueryMapperBuilder;
import org.jnosql.diana.api.document.DocumentQuery;


@ApplicationScoped
public class TagRepository {
    
    @Inject
    @Database(DatabaseType.DOCUMENT)
    OrganizationRepository organizationRepository;

    @Inject
    DocumentTemplate documentTemplate;

    @Inject
    DocumentQueryMapperBuilder docBuilder;

    /**
     * Retrieves distinct tags
     */
    public List<Tag> findAll() {
        List<Organization> organizations = findOrganzizationsByTagsLike(".*");
        return filterByPrefix("", organizations);
    }
 
    /**
     * Fetches tags by a prefix (such as "ecoviz:tag")
     */
    public List<Tag> findByPrefix(String prefix) {
        List<Organization> organizations = findOrganzizationsByTagsLike(prefix + ":*");
        return filterByPrefix(prefix, organizations);
    }

    /**
     * Finds organizations containing tags like 'like'
     */
    private List<Organization> findOrganzizationsByTagsLike(String like) {
        DocumentQuery query = docBuilder.selectFrom(Organization.class).where("tags.id").like(like).build();
        return documentTemplate.select(query); 
    }
    
    /**
     * Filters the list of tag regarding the given prefix
     */
    public List<Tag> filterByPrefix(String prefix, List<Organization> organizations) {
        Set<Tag> tags = new HashSet<>();

        organizations.parallelStream().forEach(o -> { 
            List<Tag> byPrefix = o.getTagsByPrefix(prefix);
            tags.addAll(byPrefix);
        });

        return new ArrayList<>(tags);
    }

}
