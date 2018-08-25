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
import java.util.Arrays;
import java.util.Collections;
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


	public void mergeOrganizations(String memberId, String partnerId) {
		Optional<Organization> optMember = organizationRepository.findById(memberId);
		Optional<Organization> optPart = organizationRepository.findById(partnerId);

		if(!optMember.isPresent() || !optPart.isPresent()) { return; }
		
		Organization organization = mergeOrganizations(optMember.get(), optPart.get());

		organizationRepository.deleteById(memberId); 	// Removes the member
		organizationRepository.save(organization);		// Save the up to date partner
	}

	/**
	 * Merges a member into a partner
	 * - Keeps the name of the member (which is used when importing members)
	 * - Keeps both tags list, except partner's membership
	 * - Keeps both address:
	 * 		-> locations[0] = partner's address (or member's address address if not exists)
	 * 		-> locations[1] = member's address (if exists)
	 */
	public Organization mergeOrganizations(Organization member, Organization partner) {
		logger.info("Merging member #" + member.getId() + " with partner #" + partner.getId());

		String oldName = String.valueOf(partner.getName());
		String oldMembership = String.valueOf(partner.getTagValueByPrefixOrDefault("ecoviz:membership", ""));

		Organization organization = partner;
		organization.setName(member.getName()); // Keeps member's name

		// Keeps partner's tags without membership
		List<Tag> tags = partner.getTags().stream().filter(t -> !t.getId().startsWith("ecoviz:membership")).collect(Collectors.toList());

		// Adds member's tags, and keep distinct values
		tags.addAll(member.getTags());
		tags.add(new Tag("ecoviz:old:name", oldName)); // Keeps old name of the partner
		tags.add(new Tag("ecoviz:old:membership", oldMembership));
		tags.add(Tag.make("ecoviz:is", "merged"));

		tags = new ArrayList<>(new HashSet<>(tags));
		organization.setTags(tags);
		
		organization.getLocations().addAll(member.getLocations());
	
		return organization;
	}

	/**
	 * Slipts a merged organization into two entities
	 * - The partner takes the first location
	 * - The partner takes every tags + old membership
	 * - The organization takes the others (or the first one if only one exists)
	 * - The organization takes every tags except ecoviz:projects and ecoviz:country
	 */
	public void splitOrganization(String organizationId) {		
		logger.info("Splitting organization #" + organizationId);

		Optional<Organization> optOrg = organizationRepository.findById(organizationId);
		if(!optOrg.isPresent()) { return; }

		Organization organization = optOrg.get();
		Organization[] result = splitOrganization(organization);

		organizationRepository.save(Arrays.asList(result));
	}

	public Organization[] splitOrganization(Organization organization) {
		String[] mendatoryTags = new String[] { "is:merged", "old:name", "old:membership" };

		for(String tag : mendatoryTags) {
			if(!organization.findTagById("ecoviz:" + tag).isPresent())
				throw new RuntimeException("This organization is not in a merged state");
		}

		Organization partner = organization;
		Organization member = new Organization();

		// Old values
		String oldName = organization.getName();
		String partnerMembership = organization.getTagValueByPrefixOrDefault("ecoviz:old:membership", "");
		String memberMembership = organization.getTagValueByPrefixOrDefault("ecoviz:membership", "");

		// Locations
		Address partnerLocation = Address.DEFAULT_LOCATION, memberLocation = Address.DEFAULT_LOCATION;
		if(organization.getLocations().size() == 1) {
			partnerLocation = organization.getLocations().get(0);
			memberLocation = organization.getLocations().get(0);
		} else if(organization.getLocations().size() > 1) {
			partnerLocation = organization.getLocations().get(0);
			memberLocation = organization.getLocations().get(1);
		}

		// Partner
		List<Tag> partnerTags = new ArrayList<>();
		partnerTags.addAll(organization.getTagsByPrefix("ecoviz:project"));
		partnerTags.addAll(organization.getTagsByPrefix("ecoviz:country"));
		partnerTags.addAll(organization.getTagsByPrefix("ecoviz:tag"));
		if(!partnerMembership.isEmpty()) { partnerTags.add(Tag.make("ecoviz:membership", partnerMembership)); }

		partner.setName(organization.findTagById("ecoviz:old:name").get().getName());
		partner.setTags(partnerTags);
		partner.setLocations(Arrays.asList(partnerLocation));

		// Member
		List<Tag> memberTags = new ArrayList<>();
		memberTags.add(Tag.make("ecoviz:membership", memberMembership));

		member.setName(oldName);
		member.setId(RandomHelper.uuid());
		member.setTags(memberTags);
		member.setLocations(Arrays.asList(memberLocation));

		return new Organization[] { partner, member };
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
