package fi.op.sample.oidc.domain;

import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;

import org.slf4j.LoggerFactory;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEDecrypter;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

/**
 * Decrypts ID token (using the private key from keystore)
 * 
 * Verifies ID token signature (using the public key of identity broker)
 * 
 * Extracts identity from ID token.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class IdTokenHandler {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(IdTokenHandler.class);

    private final KeystoreLoader keyLoader;

    public IdTokenHandler(KeystoreLoader keyLoader) {
        this.keyLoader = keyLoader;
    }

    public Identity extractIdentity(String idToken, KeystoreLoader keystore, JwksLoader jwksLoader) {
        try {
            EncryptedJWT e = EncryptedJWT.parse(idToken);
            String keyId = e.getHeader().getKeyID();
            PrivateKey decryptionKey = keystore.getKeyById(keyId, true).getPrivateKey();
            JWEDecrypter d = new RSADecrypter(decryptionKey);
            e.decrypt(d);
            logger.info("Decrypted id token: {}", e.getPayload().toString());

            SignedJWT decrypted = SignedJWT.parse(e.getPayload().toString());
            RSAPublicKey signatureVerificationKey = jwksLoader.getKeyById(decrypted.getHeader().getKeyID(), true)
                    .getPublicKey();
            verifySignature(decrypted, signatureVerificationKey);
            JWTClaimsSet claims = decrypted.getJWTClaimsSet();
            
            String idRawData=decrypted.getPayload().toString();
            logger.info("Payload: " + idRawData);
            idRawData = idRawData.replace(",", ",\n\t");
            idRawData = idRawData.replace("{", "{\n\t");
            idRawData = idRawData.replace("}", "\n}");            
            Identity identity = new Identity();
            identity.setIdentityRawData(idRawData);
            identity.setName(claims.getStringClaim("name"));
            identity.setSsn(claims.getStringClaim("personal_identity_code"));
            return identity;
        } catch (JOSEException | ParseException e) {
            logger.error("Error decrypting and extracting identity from id token!", e);
            throw new OidcDemoException("Error decrypting and extracting identity from id token!");
        }
    }

    public void verifySignature(SignedJWT jwt, RSAPublicKey signatureVerificationKey) throws JOSEException {
        RSASSAVerifier very = new RSASSAVerifier(signatureVerificationKey);
        boolean isMatching = jwt.verify(very);
        logger.info("ID Token signature matches: {}", isMatching);
        if (!isMatching) {
            throw new OidcDemoException("ID Token signature verification failed!");
        }
    }
}
