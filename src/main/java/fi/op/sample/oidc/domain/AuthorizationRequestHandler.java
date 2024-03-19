package fi.op.sample.oidc.domain;

import java.security.PrivateKey;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Wraps OIDC authorization request parameters into a signed Java Web Token.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class AuthorizationRequestHandler {
    // Keystore loader.
    private final KeystoreLoader keyLoader;

    /**
     * Constructor for AuthorizationRequestHandler
     *
     * @param keyLoader
     *            Keystore loader.
     */
    public AuthorizationRequestHandler(KeystoreLoader keyLoader) {
        this.keyLoader = keyLoader;
    }

    /**
     * Wraps authentication request parameters into a signed Java Web Token.
     *
     * @param params
     *            Authentication request parameters.
     */
    public OidcRequestParameters sign(OidcRequestParameters params) throws JOSEException {
        PrivateKey signingKey = keyLoader.getSigningKey().getPrivateKey();

        // Authentication request parameters documented here:
        // https://github.com/op-developer/Identity-Service-Broker-API
        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder() //
                .claim("client_id", params.getClientId()) //
                .claim("redirect_uri", params.getRedirectUri()) //
                .claim("response_type", params.getResponseType()) //
                .claim("scope", params.getScope()) //
                .claim("state", params.getState()) //
                .claim("nonce", params.getNonce()) //
                .claim("prompt", params.getPrompt()) //
                .claim("ui_locales", params.getUiLocales())
                .claim("ftn_spname", params.getSpName());

        if (params.getFtnIdpId() != null) {
            builder = builder.claim("ftn_idp_id", params.getFtnIdpId());
        }

        JWTClaimsSet claimsSet = builder.build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
            .type(JOSEObjectType.JWT)
            .keyID(keyLoader.getSigningKey().getKeyId())
            .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);
        JWSSigner signer = new RSASSASigner(signingKey);
        jwt.sign(signer);
        String result = jwt.getHeader().toBase64URL() + "." + jwt.getPayload().toBase64URL() + "."
                + jwt.getSignature().toString();
        params.setRequest(result);
        return params;
    }
}
