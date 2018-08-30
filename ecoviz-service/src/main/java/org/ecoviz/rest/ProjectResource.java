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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.StreamingOutput;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.ecoviz.domain.dto.ValueDto;
import org.ecoviz.services.ProjectService;

@Path("/projects")
@RequestScoped
public class ProjectResource {

    @Inject
    ProjectService projectService; 

    @POST
    @Path("/import")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON})
    public ValueDto importFromCsv(@RequestBody String csv) throws IOException, GeneralSecurityException {
        return projectService.importFromCsv(csv);
    }
    
    @GET
    @Path("/import/{id}/progress")
    @RolesAllowed({"admin"})
    @Produces({MediaType.APPLICATION_JSON})
    public ValueDto getProgress(@PathParam("id") String id) {
        return projectService.getImportProgress(id);
    }
    
    @GET
    @Path("/export/{type}")
    @RolesAllowed({"admin"})
    @Produces({MediaType.APPLICATION_OCTET_STREAM})
    public StreamingOutput exportProjects(@PathParam("type") String type) throws IOException {
        String result = projectService.exportProjects(type);

        return new StreamingOutput() {
			@Override
			public void write(OutputStream output) throws IOException, WebApplicationException {
				output.write(result.getBytes("UTF-8"));
			}
        };
    }
        
}
