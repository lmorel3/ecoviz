/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.ecoviz.domain.TreeData;
import org.ecoviz.services.DataService;

@RequestScoped
@Path("/data")
public class DataResource {
    
    @Inject
    private DataService dataService;
    
    @Path("/children/{parentId}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<TreeData> getChildren(@PathParam("parentId")  String parentId) {
        System.out.println("Retrieving children of " + parentId);
        return dataService.getChildren(parentId);
    } 
    
}
