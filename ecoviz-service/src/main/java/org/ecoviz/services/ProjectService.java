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
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.ecoviz.converters.ProjectConverter;
import org.ecoviz.domain.Organization;
import org.ecoviz.domain.dto.ValueDto;
import org.ecoviz.helpers.RandomHelper;
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

    private static Map<String, Integer> progressMap = new ConcurrentHashMap<>();
    
    /**
     * Launches the projects import
     * - Firstly: progress=0
     * - If it fails: progress=-1
     * - Otherwise, it's incremented until 100 
     * @throws IOException
     */
    public ValueDto importFromCsv(InputStream file) throws IOException {
        String id = RandomHelper.uuid();
        CSVParser parser = ProjectConverter.CSV_FORMAT_READ.parse(new InputStreamReader(file, "UTF-8"));
        progressMap.put(id, 0);

        Thread thread = new Thread(new Runnable(){

            @Override
            public void run() {
                List<Organization> partners = null;
                System.out.println("Launching import...");
        
                // Import data
                try {
                    partners = converter.createFromRecords(parser, id, progressMap);        
                } catch (IOException e) {
                    progressMap.put(id, -1);
        
                    Thread.currentThread().interrupt();
                    return;
                }
        
                // Saves data
                if(!partners.isEmpty()) {
                    System.out.println("Importing " + partners.size()  + " partners");
                    
                    // First, we delete everything
                    for(Organization p : organizationService.findByPrefix("ecoviz:project")) {
                        organizationRepository.deleteById(p.getId());
                    }
                
                    for(Organization o : partners) {
                        System.out.println("saving " + o);
                        save(o);
                    }
        
                    System.out.println("Projects have been imported!");
                }         
            }
        });

        thread.start();

        return ValueDto.of(id);
    }

    /**
     * Gets the progress for a given import
     */
    public ValueDto getImportProgress(String id) {
        Integer progress = progressMap.getOrDefault(id, -2);

        // Removes progress counter
        if(progress == -1 || progress >= 100) {
            progressMap.remove(id);
        }

        return ValueDto.of(progress);
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
