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

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.ecoviz.domain.Organization;
import org.ecoviz.domain.Tag;
import org.ecoviz.services.MemberService;
import org.ecoviz.services.OrganizationService;

@RequestScoped
@Path("/partners")
public class PartnerResource {

    @Inject
    OrganizationService organizationService;
    
    @Inject
    MemberService memberService;

    
    @GET
    @Path("/")
    @Produces({MediaType.APPLICATION_JSON})
    public List<Organization> getPartners() {
        return organizationService.getPartners();
    }
    
    @POST
    @Path("/{partnerId}/tags")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public void addTags(@PathParam("partnerId") String partnerId, @RequestBody List<Tag> tags) {
        organizationService.addTags(partnerId, tags);
    }

    @PUT
    @Path("/{partnerId}/tags")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public void setTags(@PathParam("partnerId") String partnerId, @RequestBody List<Tag> tags) {
        organizationService.setUserTags(partnerId, tags);
    }

    @GET
    @Path("/{partnerId}")
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.APPLICATION_JSON})
    public Organization getPartner(@PathParam("partnerId") String partnerId) {
        return memberService.findOrganizationById(partnerId);
    }

    @PUT
    @Path("/{partnerId}")
    @RolesAllowed({"admin"})
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.TEXT_PLAIN})
    public void updatePartner(@PathParam("partnerId") String partnerId, @RequestBody Organization partner) {
        memberService.updateOrganization(partnerId, partner);
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
