/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.repositories;

import java.util.List;
import java.util.Optional;

import org.ecoviz.domain.Organization;
import org.jnosql.artemis.Repository;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public interface OrganizationRepository extends Repository<Organization, String> {
    
    public List<Organization> findAll();
    public Optional<Organization> findByName(String name);
    
}
