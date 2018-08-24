/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.ecoviz.converters.ProjectConverter;
import org.ecoviz.domain.Organization;
import org.ecoviz.repositories.OrganizationRepository;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;

@ApplicationScoped
public class ProjectService {
	
    @Inject
	@Database(DatabaseType.DOCUMENT)
    private OrganizationRepository organizationRepository;
    
    @Inject
    private OrganizationService organizationService;
    
    @Inject
    private ProjectConverter converter;
    
    public void importFromCsv(InputStream file) throws IOException {
        final Reader reader = new InputStreamReader(file, "UTF-8");
        List<Organization> partners = converter.createFromRecords(ProjectConverter.CSV_FORMAT_READ.parse(reader));

        if(!partners.isEmpty()) {
            System.out.println("Importing " + partners.size() + " partners");
            
            // First, we delete everything
            for(Organization p : organizationService.findByPrefix("ecoviz:project")) {
                organizationRepository.deleteById(p.getId());
            }
            
            for(Organization o : partners) {
                System.out.println("saving " + o);
                save(o);
            }
        }
    
    }

    public String exportProjects(String type) throws IOException {
        // TODO: Handle multiple formats
        // TODO: Use an Enum
        ProjectConverter converter = new ProjectConverter();
        List<Organization> projects = organizationRepository.findAll();
        return converter.exportAsCsv(projects);
    }

    public void save(Organization o) {
        try { 
            organizationRepository.save(o);
        } catch(Exception e) {
            System.err.println("Unable to save " + o);
            e.printStackTrace();
        }
    }

}
