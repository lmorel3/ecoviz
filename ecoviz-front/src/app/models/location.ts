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
        
    number: string;
    street: string;
    city: string;
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
        if(!data) return location;

        let address: any = data.address;
        
        location.number = '';
        location.street =  address.road;
        
        if(!!address.city) location.city = address.city;
        else if(!!address.town) location.city = address.town;
        else if(!!address.village) location.city = address.village;

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
            address: {
                road: location.street,
                city: location.city,
                postcode: location.zipCode,
                country: location.country,
            },
            lat: location.latitude,
            lon: location.longitude
        }
    }

}
