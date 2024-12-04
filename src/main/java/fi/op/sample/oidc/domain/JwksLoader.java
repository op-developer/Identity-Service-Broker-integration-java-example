package fi.op.sample.oidc.domain;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import net.minidev.json.JSONObject;

/**
 * Loads JWKS keys from identity broker and caches them for some minutes.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class JwksLoader extends KeyCache {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(JwksLoader.class);

    // URL from which we get the broker's public keys.
    // This can be a proxy to JWKS URL (in high-security envs) or the JWSK URL itself (in low-security envs.)
    private String jwksProxy;

    public JwksLoader() {
        super();
    }

    @Override
    protected List<OidcKey> loadKeys() {
        try {
            // read ISB's keys from signed-jwks endpoint
            URI signedJwksUri = new URI(jwksProxy);
            String jwksTokenStr = getIsbJwks(signedJwksUri);

            // jwksToken is a signed JSON web token
            SignedJWT jwksToken = SignedJWT.parse(jwksTokenStr);

            String pubKeyFileName = Configuration.ISB_PUB_SIGNING_KEY_PEM_FILE;
            RSAPublicKey signatureVerificationKey = this.readIsbPublicKeyFile(pubKeyFileName);

            RSASSAVerifier verifier = new RSASSAVerifier(signatureVerificationKey);
            if (jwksToken.verify(verifier)) {
                logger.info("ISB JWKS signature matches");
            } else {
                throw new OidcDemoException("Verifying the ISB JWKS signature failed");
            }

            JWTClaimsSet claims = jwksToken.getJWTClaimsSet();

            // Verify ISS and SUB
            if ( !claims.getClaim("iss").toString().equals(claims.getClaim("sub").toString()) ||
            !claims.getClaim("iss").toString().equals(signedJwksUri.getScheme() + "://" + signedJwksUri.getHost())) {
                throw new OidcDemoException("Verifying ISS or SUB failed");
            }
            // verify IAT and EXP
            long timeNow = new Date().getTime();
            Date iat = (Date) claims.getClaim("iat");
            Date exp = (Date) claims.getClaim("exp");
            if (iat.getTime() >= timeNow || exp.getTime() < timeNow) {
                throw new OidcDemoException("Verifying IAT and EXP failed");
            }

            // print ISB public keys for debugging purposes
            logger.info("ISB public keys: {}", claims.getClaim("keys"));

            // create JWKSet
            JSONObject isbKeys = new JSONObject();
            isbKeys.put("keys", claims.getClaim("keys"));
            JWKSet jwkSet = JWKSet.parse(isbKeys);
            return pickSignatureKeys(jwkSet);
        } catch (Exception e ) {
            logger.error("Loading JWKS for OIDC signature verification failed!", e);
            throw new OidcDemoException("Loading JWKS for OIDC signature verification failed!", e);
        }
    }

    /**
     * Out of all keys picks the ones which can be used for sigining authentication requests, etc.
     *
     * @param publicKeys
     *            A set of keys.
     * @return Keys intended for signing.
     * @throws JOSEException
     */
    static List<OidcKey> pickSignatureKeys(JWKSet publicKeys) throws JOSEException {
        List<OidcKey> result = new ArrayList<>();
        for (JWK jwk : publicKeys.getKeys()) {
            if (jwk.getKeyUse() == KeyUse.SIGNATURE && jwk.getKeyType() == KeyType.RSA) {
                OidcKey key = new OidcKey();
                key.setPublicKey(((RSAKey) jwk).toRSAPublicKey());
                key.setShowInJwks(false);
                key.setUse(OidcKey.USE_SIGNING);
                key.setKeyId(jwk.getKeyID());
                result.add(key);
            }
        }
        if (result.isEmpty()) {
            throw new OidcDemoException("Supported signature verification keys not found in key set!");
        }
        return result;
    }

    public void setJwksProxy(String jwksProxy) {
        this.jwksProxy = jwksProxy;
    }

    private RSAPublicKey readIsbPublicKeyFile(String filename)
        throws Exception {

        String key = new String(Files.readAllBytes(Paths.get(filename)), Charset.defaultCharset());
        String publicKeyPEM = key
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replaceAll(System.lineSeparator(), "")
            .replace("-----END PUBLIC KEY-----", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(encoded);
        return (RSAPublicKey) keyFactory.generatePublic(keySpec);
    }

    private String getIsbJwks(URI uri) throws IOException {
        String jwks = RestClient.create(uri)
            .get()
            .retrieve()
            .body(String.class);

        logger.info("ISB JWKS Token: {}", jwks);
        return jwks;
    }
}
