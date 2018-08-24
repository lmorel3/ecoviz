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

/**
 * This code comes from a Microprofile sample
 * https://github.com/javaee-samples/microprofile1.2-samples
 */
import static com.nimbusds.jose.JOSEObjectType.JWT;
import static com.nimbusds.jose.JWSAlgorithm.RS256;
import static java.lang.Thread.currentThread;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import org.ecoviz.domain.User;

public class JwtHelper {

    private static String JWT_ISSUER   = "org.ecoviz";
    private static String JWT_AUDIENCE = "ecoviz"; 

    public static int TOKEN_DURATION_SEC = 3600;

    /**
     * Generates a signed JWT token, for a valid user
     */
    public static String generateJWTString(User user) throws JOSEException, InvalidKeySpecException, NoSuchAlgorithmException, IOException  {
        
        JWTClaimsSet claimsSet = createClaimsetForUser(user);

        SignedJWT signedJWT = new SignedJWT(new JWSHeader
                                            .Builder(RS256)
                                            .keyID("/privateKey.pem")
                                            .type(JWT)
                                            .build(), claimsSet);
        
        signedJWT.sign(new RSASSASigner(readPrivateKey("privateKey.pem")));
        
        return signedJWT.serialize();
    }
    
    /**
     * Extracts private key from a file
     */
    public static PrivateKey readPrivateKey(String resourceName) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException {
        byte[] byteBuffer = new byte[16384];
        int length = currentThread().getContextClassLoader()
                                    .getResource(resourceName)
                                    .openStream()
                                    .read(byteBuffer);
        
        String key = new String(byteBuffer, 0, length).replaceAll("-----BEGIN (.*)-----", "")
                                                      .replaceAll("-----END (.*)----", "")
                                                      .replaceAll("\r\n", "")
                                                      .replaceAll("\n", "")
                                                      .trim();

        return KeyFactory.getInstance("RSA")
                         .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key)));
    }

    /**
     * Instantiates a "claimset" for a user
     */
    private static JWTClaimsSet createClaimsetForUser(User user) {
      
        Instant now = Instant.now();
        Instant expirationTime = now.plusSeconds(TOKEN_DURATION_SEC);
        
        return new JWTClaimsSet.Builder()
                        .issuer(JWT_ISSUER)
                        .issueTime(Date.from(now))
                        .expirationTime(Date.from(expirationTime))
                        .jwtID(RandomHelper.uuid())
                        .audience(JWT_AUDIENCE)
                        .subject(user.getId())
                        .claim("upn", user.getUsername())
                        .claim("groups", user.getRoles())
                        .build();

    }

}
