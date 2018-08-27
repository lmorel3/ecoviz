/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.domain;

import org.ecoviz.domain.dto.AddressDto;
import org.ecoviz.domain.dto.CityDto;
import org.jnosql.artemis.Column;
import org.jnosql.artemis.Entity;

@Entity
public class Location {

    public final static Location DEFAULT_LOCATION;

    static {
        DEFAULT_LOCATION = new Location();
        DEFAULT_LOCATION.setCity("");
        DEFAULT_LOCATION.setStreet("");
        DEFAULT_LOCATION.setZipCode("");
        DEFAULT_LOCATION.setLatitude(39.292);
        DEFAULT_LOCATION.setLongitude(-40.869);
    }

    @Column
    private String street;
    
    @Column
    private String city;
    
    @Column
    private String zipCode;
    
    @Column
    private String country;
    
    @Column
    private Double longitude;
    
    @Column
    private Double latitude;
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String getStreet() {
        return street;
    }
    
    public void setCity(String name) {
        this.city = name;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public static Location fromDto(AddressDto addressDto, CityDto cityDto) {
        Location address = new Location();
        
        address.setStreet(addressDto.getStreet());
        
        address.setCity(cityDto.getName());
        address.setCountry(cityDto.getCountry());
        address.setZipCode(cityDto.getZipCode());
        
        address.setLatitude(cityDto.getLatitude());
        address.setLongitude(cityDto.getLongitude());
        
        return address;
    }

    public static Location fromDto(AddressDto addressDto) {
        Location address = new Location();
        
        address.setStreet(addressDto.getStreet());
        
        address.setCity(addressDto.getCityName());
        address.setCountry(addressDto.getCountry());
        address.setZipCode(addressDto.getZipCode());
        
        try {
            address.setLatitude(addressDto.getLatitude());
            address.setLongitude(addressDto.getLongitude());
        } catch(NumberFormatException e) {
            address.setLatitude(null);
            address.setLongitude(null);
        }
        
        return address;
    }
    
}
