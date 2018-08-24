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

import org.ecoviz.domain.Tag;
import org.ecoviz.services.TagService;

@RequestScoped
@Path("/tags")
public class TagResource {
    
    @Inject
    private TagService tagService;
    
    @Path("/")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<Tag> getTags() {
        return tagService.getTags();
    } 

    @Path("/{prefix}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<Tag> getTagsByPrefix(@PathParam("prefix") String prefix) {
        return tagService.getTagsByPrefix(prefix);
    } 
    
}
