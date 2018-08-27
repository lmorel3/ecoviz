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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ecoviz.domain.dto.OrganizationDto;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.Id;

@Entity(value = "organizations")
public class Organization {
    
    @Id
    private String id;
    
    @Column
    private String name;
    
    @Column
    private String description;

    @Column
    private List<Location> locations = new ArrayList<>();
    
    @Column
    private List<Tag> tags = new ArrayList<>();    

    public void setId(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public List<Location> getLocations() {
        return locations;
    }
    
    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void addLocation(Location location) {
        this.locations.add(location);
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public void setUserTags(List<Tag> tags) {
        List<Tag> tagsWithoutUserTags = this.tags.parallelStream()
            .filter(t -> !t.getId().startsWith("ecoviz:tag")).collect(Collectors.toList());

        this.tags = tagsWithoutUserTags;
        this.tags.addAll(tags);
    }

    public void addTag(Tag tag) {
        this.tags.add(tag);
    }

    public void addTags(List<Tag> tags) {
        this.tags.addAll(tags);
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }

    ///////////////////////

    public List<Tag> getTagsByPrefix(String prefix) {
        return tags.parallelStream().filter(t -> t.getId().startsWith(prefix)).collect(Collectors.toList());
    }

    public Optional<Tag> findTagById(String id) {
        return tags.parallelStream().filter(t -> t.getId().equals(id)).findFirst();
    }
    
    public Optional<Tag> findTagByPrefix(String prefix) {
        return tags.parallelStream().filter(t -> t.getId().startsWith(prefix)).findFirst();
    }

    public String getTagValueByPrefixOrDefault(String prefix, String defaultStr) {
        Optional<Tag> opt = findTagByPrefix(prefix);
        return opt.isPresent() ? opt.get().getName() : defaultStr; 
    }
    
    //////////////////////
    @Override
    public String toString() {
        return "Organization #" + id + " : " + name + " has " + locations.size() + " locations";
    }
    
    public static Organization fromDto(OrganizationDto dto, List<Location> address) {
        Organization member = new Organization();

        if(address == null || address.size() == 0) {
            member.addLocation(Location.DEFAULT_LOCATION);
        }
        
        member.setId(dto.getId());
        member.setName(dto.getName());
        member.setLocations(address);
        member.addTag(Tag.make("ecoviz:membership", dto.getMemberType()));
        
        return member;
    }
    
}
