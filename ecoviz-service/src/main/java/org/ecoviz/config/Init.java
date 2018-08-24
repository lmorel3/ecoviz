/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.config;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ecoviz.services.UserService;

@ApplicationScoped
@Startup
@Singleton
public class Init {

    @Inject
    UserService userService;
    
    @PostConstruct
    public void init() {  

        try {
            userService.createDefaultUserIfNeeded();
        } catch (Exception e) {
            System.err.println("Runtime exception: " + e.getMessage());
        }

    } 


}
