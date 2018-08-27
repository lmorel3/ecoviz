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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;

import com.vividsolutions.jts.util.Assert;

import org.ecoviz.domain.Location;
import org.ecoviz.domain.Organization;
import org.ecoviz.domain.Tag;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {

    MemberService memberService = new MemberService();

    @Test
    void testMerge() {
        Organization merged = merge();

        Assert.equals("Partner name", merged.findTagById("ecoviz:old:name").get().getName());
        Assert.equals("Strategic Member", merged.findTagByPrefix("ecoviz:membership").get().getName());
        Assert.equals("P", merged.findTagById("ecoviz:old:membership").get().getName());
        Assert.isTrue(merged.findTagById("ecoviz:is:merged").isPresent());

        Assert.equals("Member name", merged.getName());
        Assert.equals("partner-id", merged.getId());

        Assert.equals(2, merged.getLocations().size());
        Assert.equals(-2.0, merged.getLocations().get(0).getLatitude());
        Assert.equals(-2.1, merged.getLocations().get(0).getLongitude());

        Assert.equals(3, merged.getTagsByPrefix("ecoviz:tag").size());
        Assert.equals("FR", merged.getTagValueByPrefixOrDefault("ecoviz:country", ""));
        Assert.equals(1, merged.getTagsByPrefix("ecoviz:project").size());
    }

    @Test
    void testSplit() {
        Organization merged = merge();
        Organization[] split = memberService.splitOrganization(merged);

        Organization partner = split[0];
        Organization member = split[1];

        Assert.equals("Partner name", partner.getName());
        Assert.equals("partner-id", partner.getId());
        Assert.equals(0, partner.getTagsByPrefix("ecoviz:is").size());
        Assert.equals(0, partner.getTagsByPrefix("ecoviz:old").size());
        Assert.equals(1, partner.getTagsByPrefix("ecoviz:project").size());
        Assert.equals(3, partner.getTagsByPrefix("ecoviz:tag").size());        
        Assert.equals("FR", partner.getTagValueByPrefixOrDefault("ecoviz:country", ""));
        Assert.equals(-2.0, partner.getLocations().get(0).getLatitude());
        Assert.equals(-2.1, partner.getLocations().get(0).getLongitude());

        Assert.equals("Member name", member.getName());
        Assert.isTrue(member.getId().length() > 0);
        Assert.equals(0, member.getTagsByPrefix("ecoviz:is").size());
        Assert.equals(0, member.getTagsByPrefix("ecoviz:old").size());
        Assert.equals(0, member.getTagsByPrefix("ecoviz:project").size());
        Assert.equals(0, member.getTagsByPrefix("ecoviz:tag").size());
        Assert.equals(0, member.getTagsByPrefix("ecoviz:country").size());
        Assert.equals(-1.0, member.getLocations().get(0).getLatitude());
        Assert.equals(-1.1, member.getLocations().get(0).getLongitude());
    }

    @Test
    void testSplitFail() {
        assertThrows(RuntimeException.class, () -> { memberService.splitOrganization(new Organization()); });
    }

    Organization merge() {
        Organization partner = makePartner();
        Organization member = makeMember();
        return memberService.mergeOrganizations(member, partner);
    }

    Organization makeMember() {
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
