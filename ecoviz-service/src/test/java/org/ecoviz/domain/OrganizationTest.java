/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.domain;

import java.util.Arrays;
import java.util.List;

import com.vividsolutions.jts.util.Assert;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class OrganizationTest {

    @Test
    void testGetTagsByPrefix() {
        Organization o = new Organization();
        o.setTags(createFakeTags());

        Assert.equals(2, o.getTagsByPrefix("ecoviz:tag").size());
        Assert.equals(4, o.getTagsByPrefix("").size());
    }

    @Test
    void testSetUserTags() {
        fail("Test CI");

        Organization o = new Organization();
        o.setTags(createFakeTags());

        Tag t1 = new Tag("ecoviz:tag:it", "IT");
        Tag t2 = new Tag("ecoviz:tag:other", "Other");
        o.setUserTags(Arrays.asList(t1, t2));

        Assert.equals(2, o.getTagsByPrefix("ecoviz:tag").size());
        Assert.equals(4, o.getTags().size());
    }

    private List<Tag> createFakeTags() {
        Tag t1 = new Tag("ecoviz:project:a", "Project A");
        Tag t2 = new Tag("ecoviz:tag:university", "University");
        Tag t3 = new Tag("ecoviz:tag:it", "IT");
        Tag t4 = new Tag("ecoviz:country:FR", "FR");

        return Arrays.asList(t1, t2, t3, t4);
    }

}
