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

import java.util.List;

import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;
import org.jnosql.artemis.Id;

@Entity(value = "users")
public class User {
    
    @Id
    private String id;
    
    @Column
    private String username;
    
    @Column
    private String password;

    @Column
    private List<String> roles;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the role
     */
    public List<String> getRoles() {
        return roles;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * @param roles the roles to set
     */
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
