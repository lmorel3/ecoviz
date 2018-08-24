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

public class JwtTokenDto {

    String token;
    int expiresIn;

    public JwtTokenDto() {

    }

    public JwtTokenDto(String token, int expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }

    /**
     * @return the expiresIn
     */
    public int getExpiresIn() {
        return expiresIn;
    }

    /**
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * @param expiresIn the expiresIn to set
     */
    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    /**
     * @param token the token to set
     */
    public void setToken(String token) {
        this.token = token;
    }

}
