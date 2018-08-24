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

import java.util.UUID;

public class RandomHelper {
    
    /**
     * Generates a random UUID
     */
    public static String uuid() {
        final String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid; 
    }
    
}
