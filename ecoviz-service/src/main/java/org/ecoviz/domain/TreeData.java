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

import org.ecoviz.domain.enums.ETreeDataType;

public class TreeData {
    
    private String id;
    private String name;
    private Long childCnt = 0L;
    
    private ETreeDataType type;
    
    public TreeData() {
    }
    
    public TreeData(String id, String name, Long childCnt, ETreeDataType type) {
        this.id = id;
        this.name = name;
        this.childCnt = childCnt;
        this.type = type;
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public Long getChildCnt() {
        return childCnt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setChildCnt(Long childCnt) {
        this.childCnt = childCnt;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public ETreeDataType getType() {
        return type;
    }
    
}
