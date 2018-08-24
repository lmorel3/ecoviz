/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.domain.enums;

import java.util.regex.Pattern;

public enum ETreeDataType {
    
    ROOT,
    PROJECT,
    PARTNER,
    COUNTRY,
    TAG;

    static Pattern REGEX = Pattern.compile(":");
    
    public static ETreeDataType get(String id) {
        String[] match = REGEX.split(id);
        if(match.length > 1 && id.startsWith("ecoviz:")) {
            return ETreeDataType.valueOf(match[1].toUpperCase());
        }
        
        return PARTNER;
    }
    
}
