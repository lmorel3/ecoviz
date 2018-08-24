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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import org.jnosql.artemis.ConfigurationUnit;
import org.jnosql.diana.api.document.DocumentCollectionManager;
import org.jnosql.diana.api.document.DocumentCollectionManagerFactory;
import org.jnosql.diana.mongodb.document.MongoDBDocumentCollectionManager;


@ApplicationScoped
public class DocumentCollectionManagerProducer {

  private static final String COLLECTION = "ecoviz";

  @Inject
  @ConfigurationUnit(name = "document", fileName = "jnosql.yaml")
  private DocumentCollectionManagerFactory<MongoDBDocumentCollectionManager> managerFactory;

  @Produces
  @ApplicationScoped
  public DocumentCollectionManager getManager() {      
    return managerFactory.get(COLLECTION);
  }
  
}
