package fi.op.sample.oidc.facade;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import fi.op.sample.oidc.domain.AuthorizationCodeHandler;
import fi.op.sample.oidc.domain.AuthorizationRequestHandler;
import fi.op.sample.oidc.domain.Configuration;
import fi.op.sample.oidc.domain.IdTokenHandler;
import fi.op.sample.oidc.domain.Identity;
import fi.op.sample.oidc.domain.JwksLoader;
import fi.op.sample.oidc.domain.KeystoreLoader;
import fi.op.sample.oidc.domain.OidcDemoException;
import fi.op.sample.oidc.domain.OidcKey;
import fi.op.sample.oidc.domain.OidcRequestParameters;
import fi.op.sample.oidc.domain.OidcResponseParameters;
import net.minidev.json.JSONValue;

/**
 * OIDC Demo facade implementation.
 *
 * <p>
 * Copyright (c) 2019 OP-Palvelut Oy
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 */
public class OidcDemoFacade {

    private final Logger logger = LoggerFactory.getLogger(OidcDemoFacade.class);

    // Stores private encryption key and private signing key
    private KeystoreLoader keyLoader;
    // Retrieves identity broker's public keys
    private JwksLoader jwksLoader;
    // Creates OIDC authorization request
    private AuthorizationRequestHandler authorizationRequestHandler;
    // Exchanges authorization code to get identity token
    private AuthorizationCodeHandler authorizationCodeHandler;
    // Decrypts identity token
    private IdTokenHandler idTokenHandler;

    @Autowired
    public OidcDemoFacade() {
        String location = Configuration.KEYSTORE_LOCATION;
        keyLoader = new KeystoreLoader(location);
        jwksLoader = new JwksLoader();
        authorizationRequestHandler = new AuthorizationRequestHandler(keyLoader);
        authorizationCodeHandler = new AuthorizationCodeHandler(keyLoader);
        idTokenHandler = new IdTokenHandler();
    }

    // Once identification flow is finished, exchanges authorization code to get identity.
    public Identity extractIdentity(OidcResponseParameters response, OidcRequestParameters requestData) {
        try {
            String idToken = authorizationCodeHandler.exchangeForIdToken(response.getCode(), requestData);
            logger.info("ID Token: {}", idToken);

            jwksLoader.setJwksProxy(Configuration.JWKS_PROXY);
            return idTokenHandler.extractIdentity(idToken, keyLoader, jwksLoader);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new OidcDemoException("Error extracting identity!", e);
        }
    }

    OidcRequestParameters readFromContract(String idp, String lang, String purpose) {
        OidcRequestParameters result = new OidcRequestParameters();

        // Identity broker specific settings
        result.setClientId(Configuration.CLIENT_ID);
        result.setSpName(Configuration.FTN_SPNAME);
        result.setResponseType(Configuration.RESPONSE_TYPE);
        result.setEndpointUrl(Configuration.AUTHORIZE_URL);
        result.setJwksProxy(Configuration.JWKS_PROXY);
        result.setTokenUrl(Configuration.TOKEN_URL);
        result.setTokenProxy(Configuration.TOKEN_PROXY);
        result.setPrompt(Configuration.PROMPT);
        result.setRedirectUri(Configuration.REDIRECT_URI);

        // Identity provider specific settings
        result.setScope(Configuration.SCOPE);
        result.setUiLocales(lang);
        result.setFtnIdpId(idp);
        result.setScope(getScope(purpose));
        return result;
    }

    // Do we ask the client to identify for the purpose of identification, creating weak credentials
    // (=username & password) or for chaining strong 2FA credentials?
    private String getScope(String purpose) {
        String commonScope = "openid profile personal_identity_code";
        String extraScope = null;
        if ("normal".equals(purpose)) {
           extraScope = "";
        } else if ("weak".equals(purpose) || "strong".equals(purpose)) {
           extraScope = " " + purpose;
        } else {
           throw new OidcDemoException("Can't determine scope for purpose " + purpose);
        }
        return commonScope + extraScope;
    }

    // Creates OIDC authorization request.
    public OidcRequestParameters oidcAuthMessage(String idp, String language, String requestId, boolean doPrompt, String purpose) {

        OidcRequestParameters signedResponse = readFromContract(idp, language, purpose);

        signedResponse.setNonce(UUID.randomUUID().toString());
        signedResponse.setState(requestId);

        signedResponse.setNonce(signedResponse.getNonce().replaceAll("-", ""));
        signedResponse.setState(signedResponse.getState().replaceAll("-", ""));

        if (doPrompt) {
            signedResponse.setPrompt("consent");
        }

        try {
            authorizationRequestHandler.sign(signedResponse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new OidcDemoException("Signing authorization request failed!", e);
        }
        logger.info("Signed response: {}", signedResponse);
        return signedResponse;
    }

    // Returns Service Provider's public keys as Signed Json Web Token containing JWKS (JSON Web Key Set).
    public String getSignedJwks() {
        // get keys
        List<JWK> webKeys = new ArrayList<>();

        for (OidcKey key : keyLoader.getKeys()) {
            boolean isSigningKey = OidcKey.USE_SIGNING.equals(key.getUse());
            KeyUse keyUse = isSigningKey ? KeyUse.SIGNATURE : KeyUse.ENCRYPTION;

            if (key.getShowInJwks()) {
                // create JWKS
                RSAKey jwkKey = new RSAKey.Builder(key.getPublicKey())
                        .keyID(key.getKeyId())
                        .keyUse(keyUse)
                        .build();
                webKeys.add(jwkKey);
            }
        }
        // create JWKS
        JWKSet jwkSet = new JWKSet(webKeys);

        // create JWS
        PrivateKey entitySigningKey = keyLoader.getEntityKey().getPrivateKey();
        String keyId = keyLoader.getEntityKey().getKeyId();

        long timeNow = new Date().getTime();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(Configuration.SP_HOST)
                .subject(Configuration.SP_HOST)
                .issueTime(new Date())
                .expirationTime(new Date(timeNow + 1000 * 60 * 60 * 25)) // 25h
                .claim("keys", jwkSet.toJSONObject().get("keys"))
                .build();

        JWSHeader header = new JWSHeader
            .Builder(JWSAlgorithm.RS256)
            .type(JOSEObjectType.JWT)
            .keyID(keyId)
            .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);
        // sign it
        JWSSigner signer = new RSASSASigner(entitySigningKey);
        try {
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw new OidcDemoException("Error signing the JWKS");
        }
        return jwt.getHeader().toBase64URL() + "." + jwt.getPayload().toBase64URL() + "."
            + jwt.getSignature().toString();
    }

    // Returns Service Provider's Entity Statement as Signed Json Web Token.
    public String getEntityStatement() {

        // get Entity Statement signing key
        List<JWK> entityKeys = new ArrayList<>();
        OidcKey entityKey =  keyLoader.getEntityKey();
        RSAKey jwkKey = new RSAKey.Builder(entityKey.getPublicKey())
                .keyID(entityKey.getKeyId())
                .keyUse(KeyUse.SIGNATURE)
                .build();
        entityKeys.add(jwkKey);
         // create keyset
        JWKSet jwkSet = new JWKSet(entityKeys);

        // define signing key
        PrivateKey entitySigningKey = entityKey.getPrivateKey();
        String keyId = entityKey.getKeyId();

        // create Entity Statement JSON web token

        JsonObject openIdRelyingParty = new JsonObject();
        JsonArray redirectUris = new JsonArray();
        redirectUris.add(Configuration.REDIRECT_URI);
        openIdRelyingParty.add("redirect_uris", redirectUris);
        openIdRelyingParty.addProperty("application_type", "web");
        openIdRelyingParty.addProperty("id_token_signed_response_alg", "RS256");
        openIdRelyingParty.addProperty("id_token_encrypted_response_alg", "RSA-OAEP");
        openIdRelyingParty.addProperty("id_token_encrypted_response_enc", "A128CBC-HS256");
        openIdRelyingParty.addProperty("request_object_signing_alg", "RS256");
        openIdRelyingParty.addProperty("token_endpoint_auth_method", "private_key_jwt");
        openIdRelyingParty.addProperty("token_endpoint_auth_signing_alg", "RS256");
        openIdRelyingParty.add("client_registration_types", new JsonArray());
        openIdRelyingParty.addProperty("organization_name", "Saippuakauppias");
        openIdRelyingParty.addProperty("signed_jwks_uri", Configuration.SP_HOST + "/signed-jwks");

        JsonObject metadataJsonObject = new JsonObject();
        metadataJsonObject.add("openid_relying_party", openIdRelyingParty);

        // ten years
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, 10);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .issuer(Configuration.SP_HOST)
            .subject(Configuration.SP_HOST)
            .issueTime(new Date())
            .expirationTime(c.getTime())
            .claim("jwks", jwkSet.toJSONObject())
            .claim("metadata", JSONValue.parse(metadataJsonObject.toString()))
            .build();

        JWSHeader header = new JWSHeader
            .Builder(JWSAlgorithm.RS256)
            .type(new JOSEObjectType("entity-statement+jwt"))
            .keyID(keyId)
            .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);
        // sign the Entity Statement JWT
        JWSSigner signer = new RSASSASigner(entitySigningKey);
        try {
            jwt.sign(signer);
        } catch (JOSEException e) {
            throw new OidcDemoException("Error signing the JWKS");
        }

        return jwt.getHeader().toBase64URL() + "." + jwt.getPayload().toBase64URL() + "."
            + jwt.getSignature().toString();
    }
}
