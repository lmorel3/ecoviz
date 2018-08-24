/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.services.providers;

import java.util.List;
import org.ecoviz.domain.TreeData;

/**
 * Generic interface for data provider
 */
public interface IDataProvider {
    
    /**
     * Provides a subset of children, depending on the `parentId`
     */
    List<TreeData> get(String parentId);
    
}
