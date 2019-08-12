package fi.op.sample.oidc.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.op.sample.oidc.domain.idp.IdentityProviderList;

/**
 * List of identity providers, which are available for a specific client.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class IdentityProviderListBuilder {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(IdentityProviderListBuilder.class);

    // Url from which we retrieve the list of identity providers.
    private String idpUrl;

    /**
     * @param url
     *            The url from which to get identity providers. <br/>
     *            Can be null. If null, builds url based on configuration.
     */
    public IdentityProviderListBuilder(String url) {
        if (url == null) {
            String defaultUrl = Configuration.IDP_LIST_URL;
            if (!defaultUrl.endsWith("/")) {
                defaultUrl += "/";
            }
            defaultUrl += Configuration.CLIENT_ID;
            idpUrl = defaultUrl;
        } else {
            idpUrl = url;
        }
    }

    private String get(HttpHost target, HttpGet request) throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(target, request);

        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String jwt = reader.readLine();
        logger.info("Identity provider list: {}", jwt);

        reader.close();
        response.close();
        httpclient.close();
        return jwt;
    }

    /**
     * Retrieves the list of identity providers from the identity broker.
     * 
     * @return The list of identity providers.
     */
    public IdentityProviderList build() {
        try {
            logger.info("Listing identity providers from {}", this.idpUrl);
            URL url = new URL(this.idpUrl);
            String idTokenHost = url.getHost();
            String idTokenPath = url.getPath();
            String httpsProtocol = url.getProtocol();

            HttpHost target = new HttpHost(idTokenHost, 443, httpsProtocol);

            HttpGet request = new HttpGet(idTokenPath);

            return parse(get(target, request));
        } catch (Exception e) {
            throw new OidcDemoException("Building the list of identity providers failed!", e);
        }
    }

    static IdentityProviderList parse(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, IdentityProviderList.class);
    }
}
