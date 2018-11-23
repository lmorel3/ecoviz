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
import com.vividsolutions.jts.util.Assert;
import org.ecoviz.domain.Location;
import org.ecoviz.domain.Organization;
import org.ecoviz.domain.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MemberServiceTest {

    MemberService memberService = new MemberService();

    @Test
    public void testMerge() {
        Organization merged = merge();

        assertEquals("Partner name", merged.findTagById("ecoviz:old:name").get().getName());
        assertEquals("Strategic Member", merged.findTagByPrefix("ecoviz:membership").get().getName());
        assertEquals("P", merged.findTagById("ecoviz:old:membership").get().getName());
        Assert.isTrue(merged.findTagById("ecoviz:is:merged").isPresent());

        assertEquals("Member name", merged.getName());
        assertEquals("partner-id", merged.getId());

        assertEquals(2, merged.getLocations().size());
        assertEquals(-2.0, merged.getLocations().get(0).getLatitude(), 0.001);
        assertEquals(-2.1, merged.getLocations().get(0).getLongitude(), 0.001);

        assertEquals(3, merged.getTagsByPrefix("ecoviz:tag").size());
        assertEquals("FR", merged.getTagValueByPrefixOrDefault("ecoviz:country", ""));
        assertEquals(1, merged.getTagsByPrefix("ecoviz:project").size());
    }

    @Test
    public void testSplit() {
        Organization merged = merge();
        Organization[] split = memberService.splitOrganization(merged);

        Organization partner = split[0];
        Organization member = split[1];

        assertEquals("Partner name", partner.getName());
        assertEquals("partner-id", partner.getId());
        assertEquals(0, partner.getTagsByPrefix("ecoviz:is").size());
        assertEquals(0, partner.getTagsByPrefix("ecoviz:old").size());
        assertEquals(1, partner.getTagsByPrefix("ecoviz:project").size());
        assertEquals(3, partner.getTagsByPrefix("ecoviz:tag").size());
        assertEquals("FR", partner.getTagValueByPrefixOrDefault("ecoviz:country", ""));
        assertEquals(-2.0, partner.getLocations().get(0).getLatitude(), 0.001);
        assertEquals(-2.1, partner.getLocations().get(0).getLongitude(), 0.001);

        assertEquals("Member name", member.getName());
        assertTrue(member.getId().length() > 0);
        assertEquals(0, member.getTagsByPrefix("ecoviz:is").size());
        assertEquals(0, member.getTagsByPrefix("ecoviz:old").size());
        assertEquals(0, member.getTagsByPrefix("ecoviz:project").size());
        assertEquals(0, member.getTagsByPrefix("ecoviz:tag").size());
        assertEquals(0, member.getTagsByPrefix("ecoviz:country").size());
        assertEquals(-1.0, member.getLocations().get(0).getLatitude(), 0.001);
        assertEquals(-1.1, member.getLocations().get(0).getLongitude(), 0.001);
    }

    @Test
    public void testSplitFail() {
        //assertThrows(RuntimeException.class, () -> { memberService.splitOrganization(new Organization()); });
    }

    private Organization merge() {
        Organization partner = makePartner();
        Organization member = makeMember();
        return memberService.mergeOrganizations(member, partner);
    }

    private Organization makeMember() {
        Organization member = new Organization();

        member.setId("member-id");
        member.setName("Member name");
        
        Tag t1 = Tag.make("ecoviz:membership", "Strategic Member");
        Tag t2 = Tag.make("ecoviz:tag", "Tag A");
        Tag t3 = Tag.make("ecoviz:tag", "Tag B");
        member.setTags(Arrays.asList(t1, t2, t3));

        Location location = new Location();
        location.setLatitude(-1.0);
        location.setLongitude(-1.1);
        member.addLocation(location);

        return member;
    } 

    Organization makePartner() {
        Organization partner = new Organization();

        partner.setId("partner-id");
        partner.setName("Partner name");
        
        Tag t1 = Tag.make("ecoviz:membership", "P");
        Tag t2 = Tag.make("ecoviz:tag", "Tag A");
        Tag t3 = Tag.make("ecoviz:tag", "Tag C");
        Tag t4 = Tag.make("ecoviz:country", "FR");
        Tag t5 = Tag.make("ecoviz:project", "Project A");
        partner.setTags(Arrays.asList(t1, t2, t3, t4, t5));

        Location location = new Location();
        location.setLatitude(-2.0);
        location.setLongitude(-2.1);
        partner.addLocation(location);

        return partner;
    } 

}
