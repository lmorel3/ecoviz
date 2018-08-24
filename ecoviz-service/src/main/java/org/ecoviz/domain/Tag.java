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

import java.util.UUID;

import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;

@Entity
public class Tag {

    @Column
    private String id;
    
    @Column
    private String name;
    
    public Tag() {
    }
    
    public Tag(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Tag(String name) {
        this.name = name;
        // If not provided, the ID is function of name
        this.id = UUID.nameUUIDFromBytes(name.getBytes()).toString();
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        
        Tag tag = (Tag) o;
        return id.equals(tag.id);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id.hashCode();
        return result;
    }

    public static Tag make(String prefix, String value) {
        String id = prefix + ":" + value.replace(' ', '-').toLowerCase();
        return new Tag(id, value);
    }

}
