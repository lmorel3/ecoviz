/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.domain.dto;

public class CityDto {

    private Long osmId;
    private String name;
    private String zipCode;
    private String country;
    
    private Double latitude;
    private Double longitude;
    
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    
    public Double getLatitude() {
        return latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
    public String getCountry() {
        return country;
    }
    
    public String getName() {
        return name;
    }

    public void setOsmId(Long id) {
        this.osmId = id;
    }
    
    public Long getOsmId() {
        return osmId;
    }

}
