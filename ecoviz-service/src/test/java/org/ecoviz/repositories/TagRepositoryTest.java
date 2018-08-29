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
import java.util.Arrays;
import java.util.List;

import com.vividsolutions.jts.util.Assert;

import org.ecoviz.domain.Organization;
import org.ecoviz.domain.Tag;
import org.junit.jupiter.api.Test;

public class TagRepositoryTest {

    TagRepository tagRepository = new TagRepository();

    @Test
    void testFilterByPrefix() {
        Organization o1 = new Organization();
        o1.setTags(createFakeTags());

        Organization o2 = new Organization();
        o2.setTags(createFakeTags());
        o2.addTag(new Tag("ecoviz:tag:hello", "Hello"));

        List<Tag> tags = tagRepository.filterByPrefix("ecoviz:tag", Arrays.asList(o1, o2));
        Assert.equals(3, tags.size());

        List<Tag> tags2 = tagRepository.filterByPrefix("ecoviz:project",  Arrays.asList(o1, o2));
        Assert.equals(1, tags2.size());
    }

    private List<Tag> createFakeTags() {
        Tag t1 = new Tag("ecoviz:project:a", "Project A");
        Tag t2 = new Tag("ecoviz:tag:university", "University");
        Tag t3 = new Tag("ecoviz:tag:it", "IT");
        Tag t4 = new Tag("ecoviz:country:FR", "FR");

        List<Tag> tags = new ArrayList<>();
        tags.add(t1);
        tags.add(t2);
        tags.add(t3);
        tags.add(t4);

        return tags;
    }

}
