/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.factories;

import org.apache.commons.csv.CSVRecord;
import org.ecoviz.domain.dto.AddressDto;
import org.ecoviz.domain.dto.OrganizationDto;

public class OrganizationDtoFactory {
    
    public static OrganizationDto createFromCsv(CSVRecord record) {
        OrganizationDto dto = new OrganizationDto();
        
        AddressDto addressDto = new AddressDto();

        addressDto.setStreet(record.get(1));
        addressDto.setCityName(record.get(2));
        addressDto.setZipCode(record.get(3));
        addressDto.setCountry(record.get(4));
        
        dto.setName(record.get(0));
        dto.setMemberType(record.get(5));
        
        dto.addLocation(addressDto);
        
        return dto;
    }
    
}
