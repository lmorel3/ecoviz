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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.ecoviz.domain.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagServiceTest {

    @Test
    public void testUnicity() {

        List<Tag> tags = Arrays.asList(new Tag("1", "Tag 1"), new Tag("2", "Tag 2"), new Tag("2", "Tag 2"));

        Set<Tag> everyTags = new HashSet<>();
        everyTags.addAll(tags);

        assertEquals(2, everyTags.size());

    }

}
