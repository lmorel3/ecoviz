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
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.ecoviz.domain.Location;
import org.ecoviz.domain.Organization;
import org.ecoviz.domain.Tag;
import org.ecoviz.domain.dto.OrganizationDto;
import org.ecoviz.services.MemberService;
import org.ecoviz.services.OrganizationService;

@Path("/organizations")
@RequestScoped
public class OrganizationResource {

    @Inject
    MemberService memberService;

    @Inject
    OrganizationService organizationService;
    
    @POST
    @Path("/")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public void createOrganization(OrganizationDto member) {
        memberService.createOrganization(member);
    }

    @GET
    @Path("/")
    @RolesAllowed({"user"})
    @Produces({MediaType.APPLICATION_JSON})
    public List<Organization> findOrganizations() {
        return memberService.findOrganizations();
    }

    @POST
    @Path("/import")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.TEXT_PLAIN})
    public void importOrganizations(@RequestBody String data) throws IOException {
        System.out.println("Importing organizations");
        memberService.importOrganizations(data);
    }

    @POST
    @Path("/{organizationId}/merge")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    public void mergeOrganizations(@PathParam("organizationId") String organizationId, @RequestBody Organization partner) {
        memberService.mergeOrganizations(organizationId, partner.getId());
    }

    @POST
    @Path("/{organizationId}/split")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    public void splitOrganization(@PathParam("organizationId") String organizationId) {
        memberService.splitOrganization(organizationId);
    }

    @PUT
    @Path("/{organizationId}")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    public void updateOrganization(@PathParam("organizationId") String organizationId, @RequestBody Organization organization) {
        memberService.updateOrganization(organizationId, organization);
    }

    @PUT
    @Path("/{organizationId}/addresses/{index}")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    public void updateOrganizationAddress(@PathParam("organizationId") String organizationId, @PathParam("index") Integer index,
                                          @RequestBody Location organization) {
        memberService.updateOrganizationAddress(organizationId, index, organization);
    }

    @Path("/{organizationId}")
    @GET
    @RolesAllowed({"user", "admin"})
    @Produces({MediaType.APPLICATION_JSON})
    public Organization getOrganization(@PathParam("organizationId") String id) {
        return memberService.findOrganizationById(id);
    }

    @DELETE
    @Path("/{organizationId}")
    @RolesAllowed({"admin"})
    @Produces({MediaType.TEXT_PLAIN})
    public void deleteOrganization(@PathParam("organizationId") String id) {
        memberService.deleteOrganization(id);
    }
    
    @POST
    @Path("/{organizationId}/tags")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public void addTags(@PathParam("organizationId") String organizationId, @RequestBody List<Tag> tags) {
        organizationService.addTags(organizationId, tags);
    }

    @PUT
    @Path("/{organizationId}/tags")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public void setTags(@PathParam("organizationId") String organizationId, @RequestBody List<Tag> tags) {
        organizationService.setUserTags(organizationId, tags);
    }

    @DELETE
    @Path("/{partnerId}/tags/{tagId}")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_PLAIN})
    public void deleteTag(@PathParam("partnerId") String partnerId, @PathParam("tagId") String tagId) {
        organizationService.deleteTag(partnerId, tagId);
    }   

}
