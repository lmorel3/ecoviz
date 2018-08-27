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
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.ecoviz.domain.Location;
import org.ecoviz.domain.Organization;
import org.ecoviz.domain.Tag;
import org.ecoviz.domain.dto.AddressDto;
import org.ecoviz.domain.dto.CityDto;
import org.ecoviz.domain.dto.OrganizationDto;
import org.ecoviz.factories.OrganizationDtoFactory;
import org.ecoviz.helpers.NominatimHelper;
import org.ecoviz.helpers.RandomHelper;
import org.ecoviz.repositories.OrganizationRepository;
import org.jnosql.artemis.Database;
import org.jnosql.artemis.DatabaseType;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.document.query.DocumentQueryMapperBuilder;
import org.jnosql.diana.api.document.DocumentQuery;

@ApplicationScoped
public class OrganizationService {

	private static Logger logger = Logger.getLogger(MemberService.class.getName());
	
	@Inject
	@Database(DatabaseType.DOCUMENT)
    private OrganizationRepository organizationRepository;
	
	@Inject
	private NominatimHelper nominatimHelper;

    @Inject
    DocumentTemplate documentTemplate;

    @Inject
    DocumentQueryMapperBuilder docBuilder;
	
	/**
	 * Adds a new organization
	 * 
	 * @param Organization member
	 */
	public void createOrganization(OrganizationDto memberDto) {
	    List<Location> addresses = createLocationsWithCities(memberDto.getLocations());
	    
	    try {
	        Optional<Organization> existing = organizationRepository.findByName(memberDto.getName());
			String id = (existing.isPresent()) ? existing.get().getId() : RandomHelper.uuid();
			
			String action = (existing.isPresent()) ? "Updating" : "Creating";
			logger.info(action + " " + memberDto.getName());
	        
	        Organization member = Organization.fromDto(memberDto, addresses);
	        member.setId(id); // If already exists, we append the current id so that we update the organization
	        
	        organizationRepository.save(member);
	    } catch (Exception e) {
			logger.info("Unable to save "  + memberDto.getName() + "=>" + e.getMessage());
	        e.printStackTrace();
	        
	        throw new InternalError("Unable to save Organization");
	    }
	}

	public void updateOrganization(String organizationId, Organization organization) {
		Optional<Organization> optOrg = organizationRepository.findById(organizationId);

		// Only updates
		if(!optOrg.isPresent()) { return; }

		organization.setId(organizationId);
		organizationRepository.save(organization);
	}

	public void updateOrganizationAddress(String organizationId, Integer addressIndex, Location address) {
		Optional<Organization> optOrg = organizationRepository.findById(organizationId);
		if(!optOrg.isPresent()) { return; }

		Organization organization = optOrg.get();
		organization.getLocations().set(addressIndex, address);

		organizationRepository.save(organization);
	}
	
	/**
	 * Imports organizations in a separated thread, in order 
	 * to return a response quickly, so that there's no timeout
	 */
    public void importOrganizations(String data) throws IOException {
		logger.info("Importing organizations...");
		CSVParser records = CSVFormat.DEFAULT
									 .withFirstRecordAsHeader()
									 .parse(new StringReader(data));
		
		logger.info("Importing " + records.getRecordNumber() + "organizations");
		for (CSVRecord record : records) {
			createOrganization(OrganizationDtoFactory.createFromCsv(record));	
		}
	
	}

	////////////////////////////////////////////////

	public void setUserTags(String organizationId, List<Tag> providesTags) {
		Optional<Organization> optOrg = organizationRepository.findById(organizationId);
		if(!optOrg.isPresent()) { return; }

		List<Tag> tags = new ArrayList<>();

        // Handles new one (generates an id)
        for(Tag tag : providesTags) {
            if(tag.getId() == null) tag = Tag.make("ecoviz:tag", tag.getName());
            tags.add(tag);
        }

		Organization organization = optOrg.get();
		organization.setUserTags(tags);

		organizationRepository.save(organization);
	}

	public void addTags(String organizationId, List<Tag> tags) {
		Optional<Organization> optOrg = organizationRepository.findById(organizationId);
		if(!optOrg.isPresent()) { return; }

		Organization organization = optOrg.get();
		organization.addTags(tags);

		organizationRepository.save(organization);
	}

	public void deleteTag(String organizationId, String tagId) {
		Optional<Organization> optOrg = organizationRepository.findById(organizationId);
		if(!optOrg.isPresent()) { return; }

		Organization organization = optOrg.get();
		List<Tag> tags = organization.getTags().parallelStream()
				.filter(t -> !t.getId().equals(tagId)).collect(Collectors.toList());

		organization.setTags(tags);
		organizationRepository.save(organization);

	}

	/////////////////////////////////////////////////
	
	public List<Organization> findOrganizations() {
	    return organizationRepository.findAll();
	}

	public List<Organization> getPartners() {
		return findByPrefix("ecoviz:project");
	}
	
	public Organization findOrganizationById(String id) {
	    Optional<Organization> organization = organizationRepository.findById(id);
	    
	    if(!organization.isPresent()) { throw new NotFoundException("Organization #" + id + " not found"); }

        return organization.get();
	}
	
	public void deleteOrganization(String id) {
	    organizationRepository.deleteById(id);
	}

	/////////////////////////////////////////////////
    
    public List<Organization> findByTag(String tag) {
        DocumentQuery query = docBuilder.selectFrom(Organization.class).where("tags.id").eq(tag).build();
        return documentTemplate.select(query);
	}
	
    public List<Organization> findByPrefix(String prefix) {
        DocumentQuery query = docBuilder.selectFrom(Organization.class).where("tags.id").like(prefix + ":*").build();
        return documentTemplate.select(query);
    }
	
	///////////////////////////////////////////////////
	
	/**
	 *  Creates concrete addresses
	 */
	private List<Location> createLocationsWithCities(List<AddressDto> locations) {
	    List<Location> addresses = new LinkedList<>();
	    
	    for(AddressDto location : locations) {
	        createAddressIfCityFound(location, addresses);
	    }
	    
	    return addresses;
	}
	
	/**
	 * Instanciates an address with a city retrieved from OSM Nominatim service
	 * -> if not found, adds the default one
	 */
	private void createAddressIfCityFound(AddressDto location, List<Location> addresses) {
		Location address = Location.DEFAULT_LOCATION;

		// If it already has geolocation data, keeps it
		if(location.getLatitude() != null && location.getLongitude() != null) {
			address = Location.fromDto(location);
			addresses.add(address);
			return;
		}

	    try {
	        CityDto city = nominatimHelper.searchCity(location.getCityName(), location.getCountry(), location.getZipCode());  
	        address = Location.fromDto(location, city);
        } catch (RuntimeException e) {
            logger.info("City not found (" + location + ")");
		}
		
		addresses.add(address);
	}

}
