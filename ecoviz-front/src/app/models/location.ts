/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
export class Location {
    
    osmCityId: Number;
    
    number: string;
    street: string;
    cityName: string;
    zipCode: string;
    country: string;

    countryCode: string; // tmp

    latitude: Number;
    longitude: Number;

    hasBeenEdited: Boolean;

    /**
     * Instanciate a Location from a Nominatim API result
     * @param data
     */
    public static fromNominatim(data: any): Location {
        let location = new Location()
        let address: any = data.address;

        location.osmCityId = data.osm_id;
        
        location.number = '';
        location.street =  address.road;
        location.cityName = address.city ? address.city : address.town;
        location.zipCode = address.postcode;
        location.country = address.country;

        location.countryCode = address.country_code.toUpperCase();

        location.latitude = data.lat;
        location.longitude = data.lon;

        location.hasBeenEdited = true;

        return location
    }

    public static toNominatim(location: Location): any {
        return {
            osm_id: location.osmCityId,
            address: {
                road: location.street,
                city: location.cityName,
                zipCode: location.zipCode,
                country: location.country,
                country_code: location.countryCode,
            },
            lat: location.latitude,
            lon: location.longitude
        }
    }

}
