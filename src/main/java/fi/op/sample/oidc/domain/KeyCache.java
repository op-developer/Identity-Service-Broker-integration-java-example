package fi.op.sample.oidc.domain;

import java.util.List;

/**
 * Caches loading keys. Keys might be loaded from Java Key Store or from JWKS url.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
abstract class KeyCache {
    public static final long DEFAULT_TTL_IN_MILLIS = 5L * 60L * 1000L;

    private final long ttl;
    private long loadedAt;

    private List<OidcKey> keys;

    public KeyCache() {
        this.ttl = DEFAULT_TTL_IN_MILLIS;
    }

    public List<OidcKey> getKeys() {
        long now = System.currentTimeMillis();
        boolean cacheExpired = loadedAt + ttl < now;
        if (keys == null || cacheExpired) {
            keys = loadKeys();
            loadedAt = now;
        }
        return keys;
    }

    public void clear() {
        keys = null;
    }

    protected abstract List<OidcKey> loadKeys();

    public OidcKey getKeyById(String keyId, boolean retry) {
        for (OidcKey key : getKeys()) {
            if (keyId.equals(key.getKeyId())) {
                return key;
            }
        }

        if (retry) {
            // Key not found.
            // Maybe we are in the middle of key rotation, line 1 has offered new keys in JWKS
            // but line 2 still has old keys cached in memory. Refresh cache before giving up.
            clear();
            return getKeyById(keyId, false);
        } else {
            throw new OidcDemoException("No key with ID " + keyId);
        }
    }
}
