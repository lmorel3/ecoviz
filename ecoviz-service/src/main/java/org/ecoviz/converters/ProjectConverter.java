/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.converters;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.ecoviz.domain.Location;
import org.ecoviz.domain.Organization;
import org.ecoviz.domain.Tag;
import org.ecoviz.domain.dto.AddressDto;
import org.ecoviz.domain.dto.CityDto;
import org.ecoviz.domain.enums.EProjectCsvField;
import org.ecoviz.helpers.NominatimHelper;
import org.ecoviz.helpers.RandomHelper;

@ApplicationScoped
public class ProjectConverter {

    final static char CSV_SEPARATOR = '\n';
    final static char CSV_DELIMITER = ',';

    public final static CSVFormat CSV_FORMAT_WRITE, CSV_FORMAT_READ;

    final static Pattern TAGS_PATTERN;
    
    static {
        CSV_FORMAT_WRITE = CSVFormat.DEFAULT
                        .withHeader(EProjectCsvField.class)
                        .withRecordSeparator(CSV_SEPARATOR)
                        .withDelimiter(CSV_DELIMITER);
        
        CSV_FORMAT_READ  = CSV_FORMAT_WRITE.withFirstRecordAsHeader();

        TAGS_PATTERN = Pattern.compile(",");
    }

    @Inject
    NominatimHelper nominatimHelper;

    public ProjectConverter() {}

    // For tests
    public ProjectConverter(NominatimHelper nominatimHelper) {
        this.nominatimHelper = nominatimHelper;
    }

    /**
     * Exports projects, partners, and tags as CSV
     * Headers are available listed in enum `EProjectCsvField` 
     * 
     * @return a CSV formatted string
     */
    public String exportAsCsv(List<Organization> organizations) throws IOException {
        StringBuffer buffer = new StringBuffer();
        CSVPrinter printer = new CSVPrinter(buffer, CSV_FORMAT_WRITE);

        for(Organization organization : organizations) {
            for(String[] line : createRecord(organization)) {
                printer.printRecord((Object[])line);
            }
        }

        printer.close();
        return new String(buffer);
    }

    /**
     * Creates a list of Project, with their Partner (+ Address and Tag), from CSV data
     * @throws IOException
     */
    public List<Organization> createFromRecords(String csvData, String importId, Map<String, Integer> progressMap) throws IOException {
        System.out.println("Creating organizations from record");

        CSVParser parser = ProjectConverter.CSV_FORMAT_READ.parse(new StringReader(csvData));
        List<CSVRecord> recordsList = parser.getRecords();
        int size = recordsList.size();
        System.out.println(size + " records to process");
        
        // Used to know if a line with this project has already been processed
        Map<String, Tag> projects = new ConcurrentHashMap<>();

        // Same thing, but for partners
        Map<String, Organization> partners = new ConcurrentHashMap<>();

        int i = 1;
        for (CSVRecord record : recordsList) {
            String projectName = record.get(EProjectCsvField.PROJECT);
            String partnerName = record.get(EProjectCsvField.NAME);

            // Either this project has already been instantiated, or we instantiate it
            Tag project = projects.getOrDefault(projectName, createProjectFromRecord(record));

            // Same thing for the partner
            Organization partner = partners.getOrDefault(partnerName, createPartnerFromRecord(record));
            partner.addTag(project);

            // Append tags
            Set<Tag> userTags = new HashSet<>(partner.getTagsByPrefix("ecoviz:tag"));
            userTags.addAll(createTagsFromStr(record.get(EProjectCsvField.TAGS)));
            partner.setUserTags(new ArrayList<>(userTags));
            
            projects.put(projectName, project);
            partners.put(partnerName, partner);

            progressMap.put(importId, ((int) i * 100 / size));
            i++;
        }

        return new LinkedList<>(partners.values());
    }

    public List<Tag> createTagsFromStr(String str) {
        return TAGS_PATTERN.splitAsStream(str).map(t -> Tag.make("ecoviz:tag", t.trim())).collect(Collectors.toList());
    }

    /**
     * Instantiates a Project, depending on record's data
     */
    public Tag createProjectFromRecord(CSVRecord record) {
        String name = record.get(EProjectCsvField.PROJECT);
        return Tag.make("ecoviz:project", name);
    }

    public Organization createPartnerFromRecord(CSVRecord record) {
        Organization partner = new Organization();
        Location location = null;

        boolean hasAddress = !record.get(EProjectCsvField.LATITUDE).isEmpty() && !record.get(EProjectCsvField.LONGITUDE).isEmpty();
        if(hasAddress) {
            location = getAddressFromRecord(record);
        } else {
            location = createAddressFromRecord(record);
        }

        partner.setId(RandomHelper.uuid());
        partner.setName(record.get(EProjectCsvField.NAME));
        
        partner.addTag(Tag.make("ecoviz:country", record.get(EProjectCsvField.COUNTRY_CODE)));
        partner.setDescription(record.get(EProjectCsvField.ROLE));

        String memberShip = record.get(EProjectCsvField.MEMBERSHIP);
        if(memberShip.length() > 0) {
            partner.addTag(Tag.make("ecoviz:membership", memberShip));
        }
        
        if(location != null) {
            partner.addLocation(location);
        }
        
        return partner;
    }

    //////

    /**
     * Create a record (one line per project) for the exported CSV data
     */
    private List<String[]> createRecord(Organization organization) {
        List<String[]> lines = new LinkedList<>();

        Location location = null;
        boolean hasAddress = false;

        if(organization.getLocations().size() > 0) {
            location = organization.getLocations().get(0);
            hasAddress = location.getLatitude() != null && location.getLongitude() != null;    
        }
        
        for(Tag project : organization.getTagsByPrefix("ecoviz:project")) {
            String[] line = new String[]{
                project.getName(),
                organization.getTagValueByPrefixOrDefault("ecoviz:membership", ""),
                organization.getName(),
                organization.getTagValueByPrefixOrDefault("ecoviz:country", ""),
                organization.getDescription(),
                hasAddress ? location.getStreet() : "",
                hasAddress ? location.getCity() : "",
                hasAddress ? location.getZipCode() : "",
                hasAddress ? location.getCountry() : "",
                hasAddress ? String.valueOf(location.getLatitude()) : "",
                hasAddress ? String.valueOf(location.getLongitude()) : "",
                organization.getTagsByPrefix("ecoviz:tag").stream().map(Tag::getName).collect(Collectors.joining(","))
            };

            lines.add(line);
        }
        
        return lines;
    }

    /**
     * In case of an address is provided, but osmId nor lat/lon,
     * it tries to find this place from Nominatim API 
     */
    private Location createAddressFromRecord(CSVRecord record) {
        Location address = null;

        if(record.get(EProjectCsvField.CITY).isEmpty() || record.get(EProjectCsvField.COUNTRY).isEmpty()) {
            return Location.DEFAULT_LOCATION;
        }

        try {
            AddressDto dto = new AddressDto();
            dto.setStreet(record.get(EProjectCsvField.ADDRESS));
            
            CityDto city = nominatimHelper.searchCity(record.get(EProjectCsvField.CITY), record.get(EProjectCsvField.POSTCODE), record.get(EProjectCsvField.COUNTRY));
            
            address = Location.fromDto(dto, city);
        } catch (RuntimeException e) {
            System.err.println("Location not found for " + record.get(EProjectCsvField.CITY));
            address = Location.DEFAULT_LOCATION;
        }
        
        return address;
    }

    private Location getAddressFromRecord(CSVRecord record) {
        Location location = new Location();

        location.setCity(record.get(EProjectCsvField.CITY));
        location.setCountry(record.get(EProjectCsvField.COUNTRY));
        location.setLatitude(Double.valueOf(record.get(EProjectCsvField.LATITUDE)));
        location.setLongitude(Double.valueOf(record.get(EProjectCsvField.LONGITUDE)));
        location.setStreet(record.get(EProjectCsvField.ADDRESS));
        location.setZipCode(record.get(EProjectCsvField.POSTCODE));
    
        return location;
    }

}
