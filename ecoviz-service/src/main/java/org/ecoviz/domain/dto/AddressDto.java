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

public class AddressDto {

    private String number;
    private String street;
    private String cityName;
    private String zipCode;
    private String country;
    
    public void setNumber(String number) {
        this.number = number;
    }
    
    public void setStreet(String street) {
        this.street = street;
    }
    
    public String getNumber() {
        return number;
    }
    
    public String getStreet() {
        return street;
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
    
    public String getCityName() {
        return cityName;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public String getCountry() {
        return country;
    }
    
    @Override
    public String toString() {
        return street + ", " + cityName + " " + zipCode + " - " + country;
    }
    
}
