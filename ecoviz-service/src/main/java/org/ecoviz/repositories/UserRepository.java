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

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;

import org.ecoviz.domain.User;
import org.jnosql.artemis.Repository;

@ApplicationScoped
public interface UserRepository extends Repository<User, String>{

    public List<User> findAll();

    public Optional<User> findByUsernameAndPassword(String username, String password);
    public Optional<User> findByUsername(String username);

}
