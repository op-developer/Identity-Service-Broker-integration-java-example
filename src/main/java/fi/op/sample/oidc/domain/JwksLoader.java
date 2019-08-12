package fi.op.sample.oidc.domain;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

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
            JWKSet publicKeys = JWKSet.load(new URL(jwksProxy));
            logger.info("Loaded Java Web Key Set from the tubes!");
            return pickSignatureKeys(publicKeys);
        } catch (ParseException | JOSEException | IOException e) {
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
}
