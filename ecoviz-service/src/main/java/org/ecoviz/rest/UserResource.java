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

import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.ecoviz.domain.User;
import org.ecoviz.domain.dto.JwtTokenDto;
import org.ecoviz.domain.dto.UserDto;
import org.ecoviz.services.UserService;

@Path("/users")
@RequestScoped
public class UserResource {

    @Context
    private SecurityContext securityContext;

    @Inject
    private JsonWebToken jwt;

    @Inject
    private UserService userService;

    @Path("/login")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public JwtTokenDto login(UserDto user) throws Exception {
        return userService.login(user.getUsername(), user.getPassword());
    }
    
    @Path("/password")
    @PUT
    @Consumes({MediaType.TEXT_PLAIN})
    @Produces({MediaType.TEXT_PLAIN})
    public void editPassword(String password) throws Exception {
        userService.setPassword(securityContext.getUserPrincipal().getName(), password);
	}
    
    @Path("/")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"admin"})
    public User createUser(UserDto userDto) throws NoSuchAlgorithmException {
        System.out.println(securityContext.getUserPrincipal().getName() + " is trying to create a new user " + userDto.getUsername());

        return userService.createUser(userDto);
    }

    @Path("/")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"admin"})
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @Path("/{userId}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed({"admin"})
    public void deleteUser(@PathParam("userId") String userId) {
        if(jwt.getSubject().equals(userId)) {
            throw new ForbiddenException();
        }

        userService.deleteUser(userId);
    }

}
