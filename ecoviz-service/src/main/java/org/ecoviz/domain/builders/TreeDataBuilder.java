/*******************************************************************************
 * Copyright (C) 2018 Eclipse Foundation
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 ******************************************************************************/
package org.ecoviz.domain.builders;

import org.ecoviz.domain.enums.ETreeDataType;
import org.ecoviz.domain.TreeData;

public class TreeDataBuilder {

    private String id;
    private String name;
    private Long childCnt;
    private ETreeDataType type;
    
    public TreeDataBuilder id(String id) {
        this.id = id;
        return this;
    }
    
    public TreeDataBuilder name(String name) {
        this.name = name;
        return this;
    }
    
    public TreeDataBuilder childCnt(Long childCnt) {
        this.childCnt = childCnt;
        return this;
    }
    
    public TreeDataBuilder type(ETreeDataType type) {
        this.type = type;
        return this;
    }
    
    public TreeData build() {
        return new TreeData(id, name, childCnt, type);
    }

}
