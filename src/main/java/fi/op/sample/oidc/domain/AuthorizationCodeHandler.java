package fi.op.sample.oidc.domain;

import java.net.URI;
import java.security.PrivateKey;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Uses OIDC protocol to exchange an authorization code for an ID token.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class AuthorizationCodeHandler {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(AuthorizationCodeHandler.class);
    private final KeystoreLoader keyLoader;

    public AuthorizationCodeHandler(KeystoreLoader keyLoader) {
        this.keyLoader = keyLoader;
    }

    public String exchangeForIdToken(String authorizationCode, OidcRequestParameters contract) throws Exception {
        URI uri = new URI(contract.getTokenProxy());

        MultiValueMap<String, String> params = MultiValueMap.fromSingleValue(Map.of(
            "code", authorizationCode,
            "grant_type", "authorization_code",
            "client_assertion_type", "urn:ietf:params:oauth:client-assertion-type:jwt-bearer",
            "client_assertion", createSignedClientAssertion(contract)));

        logger.info("Executing POST request to {}", uri);
        String response = RestClient.create(uri)
            .post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(params)
            .retrieve()
            .body(String.class);

        return parseIdToken(response);
    }

    private String parseIdToken(String jsonResponse) throws JacksonException {
        Map<String, Object> resp = new ObjectMapper().readValue(jsonResponse, new TypeReference<Map<String, Object>>() { });
        return String.valueOf(resp.get("id_token"));
    }

    private String createSignedClientAssertion(OidcRequestParameters params) throws JOSEException {
        PrivateKey signingKey = keyLoader.getSigningKey().getPrivateKey();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder() //
                .issuer(params.getClientId()) //
                .subject(params.getClientId()) //
                .audience(params.getTokenUrl()).jwtID(UUID.randomUUID().toString()) //
                .expirationTime(new Date(new Date().getTime() + 600l * 1000l)) //
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
            .type(JOSEObjectType.JWT)
            .keyID(keyLoader.getSigningKey().getKeyId())
            .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);
        JWSSigner signer = new RSASSASigner(signingKey);
        jwt.sign(signer);
        return jwt.getHeader().toBase64URL() + "." + jwt.getPayload().toBase64URL() + "."
                + jwt.getSignature().toString();
    }
}
