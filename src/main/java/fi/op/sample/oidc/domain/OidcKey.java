package fi.op.sample.oidc.domain;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Stores a private key and metadata to present the derived public key in JWKS URL.
 * 
 * Private keys are stored in FTN Keystore.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class OidcKey {
    public static final String USE_ENCRYPTION = "enc";
    public static final String USE_SIGNING = "sig";

    // Private key
    private PrivateKey privateKey;

    // Public key derived from private key.
    private RSAPublicKey publicKey;

    // Alias in Java Key Store
    private String alias;

    // Encryption or signing?
    private String use;

    // Only the newest keys are distributed at JWKS URL (showInJwks = true.)
    // Previous key is accepted for decryption but no more shown in JWKS. (showInJwks = false.)
    private boolean showInJwks;

    // JWKS Key ID (kid)
    private String keyId;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(RSAPublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUse() {
        return use;
    }

    public void setUse(String use) {
        this.use = use;
    }

    public boolean getShowInJwks() {
        return showInJwks;
    }

    public void setShowInJwks(boolean showInJwks) {
        this.showInJwks = showInJwks;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}
