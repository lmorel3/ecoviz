/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.helpers;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import org.apache.http.impl.client.DefaultHttpClient;
import org.ecoviz.domain.dto.CityDto;

import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.client.request.NominatimSearchRequest;
import fr.dudie.nominatim.client.request.paramhelper.PolygonFormat;
import fr.dudie.nominatim.model.Address;
import fr.dudie.nominatim.model.Element;

@ApplicationScoped
public class NominatimHelper {

    private static String NOMINATIM_URL = "https://nominatim.openstreetmap.org/";

    private static JsonNominatimClient client;
    
    @PostConstruct
    public void postConstruct() {
        client = new JsonNominatimClient(NOMINATIM_URL, new DefaultHttpClient(), "test@test.fr"); 
    }
    
    public CityDto searchCity(String name, String country, String zipCode) throws RuntimeException {
        
        NominatimSearchRequest request = new NominatimSearchRequest();
        request.setPolygonFormat(PolygonFormat.GEO_JSON);
        request.setQuery(name + ", " + zipCode + ", " + country);

        CityDto city = null;

        try {
            final List<Address> addresses = client.search(request);
            Address found = addresses.get(0);
            
            city = new CityDto();
            city.setOsmId(Long.valueOf(found.getOsmId()));
            city.setLatitude(found.getLatitude());
            city.setLongitude(found.getLongitude());   
            city.setCountry(country);
            city.setName(name);
            city.setZipCode(zipCode);
            
        } catch (IOException | IndexOutOfBoundsException e) {
            throw new RuntimeException("Unable to fetch data from Nominatim API");
        }
        
        return city;
        
    }
    
    public static String getElementValue(Element[] elements, String key) {
        String result = "";
        
        for(Element el : elements) {
            if(el.getKey().equals(key)) {
                result = el.getValue();
                break;
            }
        }
        
        return result;
    }

}
