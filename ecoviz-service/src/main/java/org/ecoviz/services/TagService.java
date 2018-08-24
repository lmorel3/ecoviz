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

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ecoviz.domain.Tag;
import org.ecoviz.repositories.TagRepository;

@ApplicationScoped
public class TagService {
    
	@Inject
	private TagRepository tagRepository;

    /**
     * Generates a List of unique tags
     */
    public List<Tag> getTags() {
        return tagRepository.findAll();
    }

    /**
     * 
     */
    public List<Tag> getTagsByPrefix(String prefix) {
        return tagRepository.findByPrefix(prefix);
    }
}
