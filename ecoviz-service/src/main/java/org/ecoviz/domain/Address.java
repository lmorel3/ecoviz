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
public class Address {

    public final static Address DEFAULT_LOCATION;

    static {
        DEFAULT_LOCATION = new Address();
        DEFAULT_LOCATION.setCityName("");
        DEFAULT_LOCATION.setOsmCityId(-1L);
        DEFAULT_LOCATION.setStreet("");
        DEFAULT_LOCATION.setZipCode("");
        DEFAULT_LOCATION.setLatitude(39.292);
        DEFAULT_LOCATION.setLongitude(-40.869);
    }

    @Column
    private String street;
    
    @Column
    private Long osmCityId;
    
    @Column
    private String cityName;
    
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
    
    public void setOsmCityId(Long id) {
        this.osmCityId = id;
    }
    
    public void setCityName(String name) {
        this.cityName = name;
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
    
    public Long getOsmCityId() {
        return osmCityId;
    }
    
    public String getCityName() {
        return cityName;
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
    
    public static Address fromDto(AddressDto addressDto, CityDto cityDto) {
        Address address = new Address();
        
        address.setStreet(addressDto.getStreet());
        
        address.setOsmCityId(cityDto.getOsmId());
        address.setCityName(cityDto.getName());
        address.setCountry(cityDto.getCountry());
        address.setZipCode(cityDto.getZipCode());
        
        address.setLatitude(cityDto.getLatitude());
        address.setLongitude(cityDto.getLongitude());
        
        return address;
    }
    
}
