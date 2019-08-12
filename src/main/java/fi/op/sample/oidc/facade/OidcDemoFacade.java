package fi.op.sample.oidc.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

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
        idTokenHandler = new IdTokenHandler(keyLoader);
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

    // Returns user's public keys as Java Web Key Set.
    public String getJwks() {
        List<JWK> webKeys = new ArrayList<>();

        for (OidcKey key : keyLoader.getKeys()) {
            boolean isSigningKey = OidcKey.USE_SIGNING.equals(key.getUse());
            KeyUse keyUse = isSigningKey ? KeyUse.SIGNATURE : KeyUse.ENCRYPTION;

            if (key.getShowInJwks()) {
                RSAKey jwkKey = new RSAKey.Builder(key.getPublicKey()) //
                        .keyID(key.getKeyId()) //
                        .keyUse(keyUse) //
                        .build();
                webKeys.add(jwkKey);
            }
        }

        JWKSet set = new JWKSet(webKeys);
        return set.toJSONObject().toString();
    }
}
