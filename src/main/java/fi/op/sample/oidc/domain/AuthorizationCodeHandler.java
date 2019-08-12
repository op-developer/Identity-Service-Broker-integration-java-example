package fi.op.sample.oidc.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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

        URL url = new URL(contract.getTokenProxy());
        String idTokenHost = url.getHost();
        String idTokenPath = url.getPath();
        String httpsProtocol = url.getProtocol();

        HttpHost target = new HttpHost(idTokenHost, 443, httpsProtocol);

        HttpPost request = new HttpPost(idTokenPath);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("code", authorizationCode));
        params.add(new BasicNameValuePair("grant_type", "authorization_code"));
        params.add(new BasicNameValuePair("client_assertion_type",
                "urn:ietf:params:oauth:client-assertion-type:jwt-bearer"));
        params.add(new BasicNameValuePair("client_assertion", createSignedClientAssertion(contract)));

        request.setEntity(new UrlEncodedFormEntity(params));
        logger.info("Executing request {} to {}", request.getRequestLine(), target);

        return parseIdToken(post(target, request));
    }

    String post(HttpHost target, HttpPost request) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(target, request);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String jwt = reader.readLine();
        logger.info("ID token: {}", jwt);

        reader.close();
        response.close();
        httpclient.close();
        return jwt;
    }

    String parseIdToken(String jsonResponse) {
        JsonElement jelement = new JsonParser().parse(jsonResponse);
        JsonObject jobject = jelement.getAsJsonObject();
        return jobject.get("id_token").getAsString();
    }

    String createSignedClientAssertion(OidcRequestParameters params) throws JOSEException {
        PrivateKey signingKey = keyLoader.getSigningKey().getPrivateKey();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder() //
                .issuer(params.getClientId()) //
                .subject(params.getClientId()) //
                .audience(params.getTokenUrl()).jwtID(UUID.randomUUID().toString()) //
                .expirationTime(new Date(new Date().getTime() + 600l * 1000l)) //
                .build();

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256).type(JOSEObjectType.JWT).build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);
        JWSSigner signer = new RSASSASigner(signingKey);
        jwt.sign(signer);
        return jwt.getHeader().toBase64URL() + "." + jwt.getPayload().toBase64URL() + "."
                + jwt.getSignature().toString();
    }
}
