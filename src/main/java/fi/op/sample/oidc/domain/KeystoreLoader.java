package fi.op.sample.oidc.domain;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyType;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;

import jakarta.el.PropertyNotFoundException;

/**
 * Loads OIDC keys from keystore.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class KeystoreLoader extends KeyCache {
    private final Logger logger = LoggerFactory.getLogger(KeystoreLoader.class);

    private static final String KEY_ID = "ftn.keystore.key.%s.kid";
    private static final String KEY_USE = "ftn.keystore.key.%s.use";
    private static final String SHOW_KEY_IN_JWKS = "ftn.keystore.key.%s.show_in_jwks";
    private static final String KEY_ALIAS = "ftn.keystore.key.%s.alias";

    private final String keystoreLocation;

    private KeyStore keystore;

    public KeystoreLoader(String location) {
        keystoreLocation = location;
    }

    /**
     * Loads a key from keystore.
     *
     * @param keyAlias
     *            Key name.
     * @return The key
     */
    public Key getKeyFromJKS(String keyAlias) {
        Key key = null;

        try {
            maybeLoadKeystore();
            key = keystore.getKey(keyAlias, readPassword().toCharArray());
        } catch (NoSuchAlgorithmException | KeyStoreException | UnrecoverableKeyException e) {
            logger.info(e.getMessage());
            throw new OidcDemoException("Loading keystored failed!", e);
        }

        if (key == null) {
            throw new OidcDemoException("No such key in OIDC key store:" + keyAlias);
        }

        return key;
    }

    public Properties getProperties() {
        InputStream stream = IdTokenHandler.class.getClassLoader().getResourceAsStream("application.properties");
        Properties properties = new Properties();
        try {
            properties.load(stream);
        } catch (IOException e) {
            throw new OidcDemoException("Couldn't load application properties!", e);
        }
        return properties;
    }

    public String readPassword() {
        return (String) getProperties().get("oidc.keystore.password");
    }

    void maybeLoadKeystore() {
        if (keystore == null) {
            InputStream stream = null;
            try {
                stream = IdTokenHandler.class.getClassLoader().getResourceAsStream(keystoreLocation);
                String password = readPassword();

                keystore = KeyStore.getInstance(KeyStore.getDefaultType());
                keystore.load(stream, password.toCharArray());
            } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
                logger.error("Loading keystore failed!");
                throw new OidcDemoException("Loading keystore failed!", e);
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException e) {
                        logger.error("Closing keystore stream failed! " + e.getMessage());
                        e.printStackTrace();
                        // nothing to be done
                    }
                }
            }
        }
    }

    public RSAPublicKey getVerificationKeyFromJWKS(String jwksProxyUrl) {
        RSAPublicKey publicKey = null;
        try {
            JWKSet publicKeys = JWKSet.load(new URL(jwksProxyUrl));
            publicKey = pickSignatureKey(publicKeys);
        } catch (ParseException | JOSEException | IOException e) {
            logger.error("Loading JWKS for OIDC signature verification failed!");
            throw new OidcDemoException("Loading JWKS for OIDC signature verification failed!", e);
        }

        return publicKey;
    }

    RSAPublicKey pickSignatureKey(JWKSet publicKeys) throws JOSEException {
        for (JWK jwk : publicKeys.getKeys()) {
            if (jwk.getKeyUse() == KeyUse.SIGNATURE && jwk.getKeyType() == KeyType.RSA) {
                return ((RSAKey) jwk).toRSAPublicKey();
            }
        }
        throw new OidcDemoException("Supported signature verification not found in key set!");
    }

    @Override
    protected List<OidcKey> loadKeys() {
        Properties p = getProperties();
        String keyNames = p.getProperty("ftn.keystore.keys");
        List<OidcKey> result = new ArrayList<>();

        for (String keyName : keyNames.split(",")) {
            try {
                OidcKey oidcKey = new OidcKey();
                String keyId = p.getProperty(String.format(KEY_ID, keyName));
                String use = p.getProperty(String.format(KEY_USE, keyName));
                boolean doShow = Boolean.parseBoolean(p.getProperty(String.format(SHOW_KEY_IN_JWKS, keyName)));
                String alias = p.getProperty(String.format(KEY_ALIAS, keyName));

                oidcKey.setKeyId(keyId);
                oidcKey.setUse(use);
                oidcKey.setShowInJwks(doShow);
                oidcKey.setAlias(alias);

                RSAPrivateCrtKey key = (RSAPrivateCrtKey) getKeyFromJKS(alias);

                oidcKey.setPrivateKey(key);
                oidcKey.setPublicKey(derivePublicKey(key));

                result.add(oidcKey);
            } catch (PropertyNotFoundException e) {
                logger.error("Could not read key metadata from properties!", e);
                // Go on with the next key
            }
        }
        return result;
    }

    private RSAPublicKey derivePublicKey(RSAPrivateCrtKey privateKey) {
        try {
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(privateKey.getModulus(), privateKey.getPublicExponent());
            return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.error("Error generating public key", e);
            throw new OidcDemoException("Couldn't derive public key to be shown in JWKS URL from private key!", e);
        }
    }

    public OidcKey getSigningKey() {
        OidcKey fallback = null;
        for (OidcKey key : getKeys()) {
            // It must have correct role, and preferably one displayed in JWKS.
            if (OidcKey.USE_SIGNING.equals(key.getUse())) {
                fallback = key;
                if (key.getShowInJwks()) {
                    return key;
                }
            }
        }

        return fallback;
    }

    public OidcKey getEntityKey() {
        for (OidcKey key : getKeys()) {
            // It must match the alias of the entity key
            if (key.getAlias().equals("sandbox-sp-entity-key")) {
                return key;
            }
        }

        return null;
    }

}
