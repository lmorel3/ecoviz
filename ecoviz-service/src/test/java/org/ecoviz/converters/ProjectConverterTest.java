/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.converters;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.vividsolutions.jts.util.Assert;

import org.ecoviz.domain.Location;
import org.ecoviz.domain.Tag;
import org.ecoviz.helpers.NominatimHelper;
import org.junit.jupiter.api.Test;

public class ProjectConverterTest {

    private static String CSV_DATA = "PROJECT,MEMBERSHIP,NAME,COUNTRY_CODE,ROLE,ADDRESS,CITY,POSTCODE,COUNTRY,LATITUDE,LONGITUDE,TAGS\n" + 
        "My Project,MEMBERSHIP,Partner 1,FR,ROLE,Avenue Gaston Berger,Villeurbanne,69100,France,-0.5,1.2,University\n" + 
        "My Project,MEMBERSHIP,Partner 2,ES,ROLE,,,,,,,,\"University,Autre\"\n";

    static ProjectConverter converter;
    
    static {
        //TODO: Use application context and injection mechanism
        NominatimHelper nominatimHelper = new NominatimHelper();
        nominatimHelper.postConstruct();

        converter = new ProjectConverter(nominatimHelper);
    }

    @Test
    public void testCreateTagsFromStr() {
        List<Tag> tags = converter.createTagsFromStr("Tag 1, Tag 2, Tag 3");

        Assert.equals(3, tags.size());
    }
    
}
