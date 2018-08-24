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
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.ecoviz.domain.Address;
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

@ApplicationScoped
public class MemberService {

	private static Logger logger = Logger.getLogger(MemberService.class.getName());
	
	@Inject
	@Database(DatabaseType.DOCUMENT)
    private OrganizationRepository organizationRepository;
	
	@Inject
	private NominatimHelper nominatimHelper;
	
	/**
	 * Adds a new organization
	 * 
	 * @param Organization member
	 */
	public void createOrganization(OrganizationDto memberDto) {
	    List<Address> addresses = createLocationsWithCities(memberDto.getLocations());
	    
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

	public void updateOrganizationAddress(String organizationId, Integer addressIndex, Address address) {
			Optional<Organization> optOrg = organizationRepository.findById(organizationId);
			if(!optOrg.isPresent()) { return; }
	
			Organization organization = optOrg.get();
			organization.getLocations().set(addressIndex, address);

			organizationRepository.save(organization);
	}

	/**
	 * Merges an organization into a partner
	 * - Keeps the name of the orgnanization (which is used when importing organizations)
	 * - Keeps both tags list, except partner's membership
	 * - Keeps both address except (except if they have the same zipcode)
	 */
	public void mergeOrganizations(String organizationId, String partnerId) {
		Optional<Organization> optOrg = organizationRepository.findById(organizationId);
		Optional<Organization> optPart = organizationRepository.findById(partnerId);

		if(!optOrg.isPresent() || !optPart.isPresent()) { return; }

		Organization organization = optPart.get();
		organization.setName(optOrg.get().getName()); // Keeps organization's name

		// Keeps partner's tags without membership
		List<Tag> tags = organization.getTags().stream().filter(t -> !t.getId().startsWith("ecoviz:membership")).collect(Collectors.toList());

		// Adds organization's tags, and keep distinct values
		tags.addAll(optPart.get().getTags());
		tags.add(new Tag("ecoviz:oldname", optPart.get().getName())); // Keeps old name of the partner
		tags.add(new Tag("ecoviz:oldmembership", optPart.get().getTagValueByPrefixOrDefault("ecoviz:membership", "")));

		tags = new ArrayList<>(new HashSet<>(tags));

		Address baseLocation = organization.getLocations().stream().findFirst().orElse(null);
		for(Address location : optPart.get().getLocations()) {
			if(baseLocation != null && !location.getZipCode().equals(baseLocation.getZipCode())) {
				organization.addLocation(location);
			}
		} 

		organizationRepository.deleteById(optOrg.get().getId()); // Removes organization
		organizationRepository.save(organization);			     // Save the up to date partner
	}

	/**
	 * Slipts a merged organization into two entities
	 * - The partner takes the first location
	 * - The partner takes every tags + old membership
	 * - The organization takes the others (or the first one if only one exists)
	 * - The organization takes every tags except ecoviz:projects and ecoviz:country
	 */
	public void splitOrganization(String organizationId) {
		Optional<Organization> optOrg = organizationRepository.findById(organizationId);
		if(!optOrg.isPresent()) { return; }

		Organization organization = optOrg.get();
		if(organization.getTagsByPrefix("ecoviz:old").isEmpty()) { return; }



	}
	
	/**
	 * Imports organizations in a separated thread, in order 
	 * to return a response quickly, so that there's no timeout
	 */
    public void importOrganizations(String data) throws IOException {
		logger.info("Importing organizations...");
		CSVParser records = CSVFormat.DEFAULT.parse(new StringReader(data));
		
		logger.info("Importing " + (records.getRecordNumber()-1) + "organizations");
		Thread thread = new Thread(new Runnable(){
		
			@Override
			public void run() {
				boolean isFirst = true;
				
				for (CSVRecord record : records) {
					
					if(!isFirst) {
						try {
							createOrganization(OrganizationDtoFactory.createFromCsv(record));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
					if(isFirst) { isFirst = false; }
					
				}			

				logger.info("Import completed");
			}
		});

		thread.start();
	
	}
    
	public List<Organization> findOrganizations() {
	    return organizationRepository.findAll();
	}

	public Organization findOrganizationById(String id) {
	    Optional<Organization> organization = organizationRepository.findById(id);
	    
	    if(!organization.isPresent()) { throw new NotFoundException("Organization #" + id + " not found"); }

        return organization.get();
	}
	
	public void deleteOrganization(String id) {
	    organizationRepository.deleteById(id);
	}
	
	///////////////////////////////////////////////////
	
	/**
	 *  Creates concrete addresses
	 */
	private List<Address> createLocationsWithCities(List<AddressDto> locations) {
	    List<Address> addresses = new LinkedList<>();
	    
	    for(AddressDto location : locations) {
	        createAddressIfCityFound(location, addresses);
	    }
	    
	    return addresses;
	}
	
	/**
	 * Instanciates an address with a city retrieved from OSM Nominatim service
	 */
	private void createAddressIfCityFound(AddressDto location, List<Address> addresses) {
	    try {
	        CityDto city = nominatimHelper.searchCity(location.getCityName(), location.getCountry(), location.getZipCode());
	        
	        Address address = Address.fromDto(location, city);
            addresses.add(address);
        } catch (RuntimeException e) {
            logger.info("City not found (" + location + ")");
        }
	}

}
