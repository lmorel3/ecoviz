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

import org.ecoviz.domain.Address;
import org.ecoviz.domain.Tag;
import org.ecoviz.helpers.NominatimHelper;
import org.junit.jupiter.api.Test;

public class ProjectConverterTest {

    private static String CSV_DATA = "PROJECT,MEMBERSHIP,NAME,COUNTRY_CODE,ROLE,ADDRESS,CITY,POSTCODE,COUNTRY,LATITUDE,LONGITUDE,OSM_ID,TAGS\n" + 
        "My Project,MEMBERSHIP,Partner 1,FR,ROLE,Avenue Gaston Berger,Villeurbanne,69100,France,-0.5,1.2,-1,University\n" + 
        "My Project,MEMBERSHIP,Partner 2,ES,ROLE,,,,,,,,\"University,Autre\"\n";

    static ProjectConverter converter;
    
    static {
        //TODO: Use application context and injection mechanism
        NominatimHelper nominatimHelper = new NominatimHelper();
        nominatimHelper.postConstruct();

        converter = new ProjectConverter(nominatimHelper);
    }
/*
    @Test
    public void testExportAsCsv() throws IOException {

        Project project = getFakeProject();
        String csv  = converter.exportAsCsv(Arrays.asList(project));

        Assert.equals(CSV_DATA, csv);
    }

    @Test
    public void testImportFromCsv() throws IOException {
        List<Project> projects = converter.createFromRecords(ProjectConverter.CSV_FORMAT_READ.parse(new StringReader(CSV_DATA)));

        // Projects
        Assert.equals(1, projects.size());

        // Project
        Project project = projects.get(0);
        Assert.equals("My Project", project.getName());
        assertNotNull(project.getId());

        Assert.equals(2, project.getPartners().size());

        // Partners
        Partner partner1 = project.getPartners().get(0);
        assertNotNull(partner1.getLocation());

        Partner partner2 = project.getPartners().get(1);
        Assert.equals("Partner 2", partner2.getName());
        Assert.equals("ES", partner2.getCountry());
        assertNull(partner2.getLocation());

        // Tags
        Assert.equals(2, partner2.getTags().size());
        Assert.equals("University", partner2.getTags().get(0).getName());
        assertNotNull(partner2.getTags().get(0).getName());
    
        // Location
        Address address = partner1.getLocation();
        Assert.equals("Avenue Gaston Berger", address.getStreet());
        Assert.equals("69100", address.getZipCode());
        Assert.equals("Villeurbanne", address.getCityName());
        Assert.equals("France", address.getCountry());
        Assert.equals(-0.5, address.getLatitude());
        Assert.equals(1.2, address.getLongitude());
        Assert.equals(-1L, address.getOsmCityId());
    }

    private Project getFakeProject() {

        List<Partner> partners = new LinkedList<>();
        
        Address location = new Address();
        location.setCityName("Villeurbanne");
        location.setCountry("France");
        location.setLatitude(-0.5);
        location.setLongitude(1.2);
        location.setStreet("Avenue Gaston Berger");
        location.setZipCode("69100");
        location.setOsmCityId(-1L);

        Partner p1 = new Partner();
        p1.setId("p1");
        p1.setName("Partner 1");
        p1.setCountry("FR");
        p1.setRole("Super role");
        p1.setLocation(location);
        p1.setTags(Arrays.asList(new Tag("1", "University")));

        Partner p2 = new Partner();
        p2.setId("p2");
        p2.setName("Partner 2");
        p2.setCountry("ES");
        p2.setRole("Role of p2");
        p2.setLocation(null);
        p2.setTags(Arrays.asList(new Tag("1", "University"), new Tag("2", "Autre")));

        partners.add(p1);
        partners.add(p2);

        Project project = new Project();
        project.setId("xxxx");
        project.setName("My Project");
        project.setPartners(partners);

        return project;

    }
*/   

    @Test
    public void testCreateTagsFromStr() {
        List<Tag> tags = converter.createTagsFromStr("Tag 1, Tag 2, Tag 3");

        Assert.equals(3, tags.size());
    }
    
}
