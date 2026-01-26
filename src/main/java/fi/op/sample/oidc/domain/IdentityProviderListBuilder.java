// SPDX-FileCopyrightText: Copyright 2026 OP Pohjola (https://op.fi)
//
// SPDX-License-Identifier: MIT

package fi.op.sample.oidc.domain;

import java.io.IOException;
import java.net.URI;

import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;

import fi.op.sample.oidc.domain.idp.IdentityProviderList;

public class IdentityProviderListBuilder {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(IdentityProviderListBuilder.class);

    // Url from which we retrieve the list of identity providers.
    private String idpUrl;

    /**
     * @param url The url from which to get identity providers.
     */
    public IdentityProviderListBuilder(String url) {
        idpUrl = url;
    }

    /**
     * Get identity providers from configured url.
     */
    public IdentityProviderListBuilder() {
        this(getConfiguredUrl());
    }

    private static String getConfiguredUrl() {
        String defaultUrl = Configuration.IDP_LIST_URL;
        if (!defaultUrl.endsWith("/")) {
            defaultUrl += "/";
        }
        defaultUrl += Configuration.CLIENT_ID;
        return defaultUrl;
    }

    /**
     * Retrieves the list of identity providers from the identity broker.
     *
     * @return The list of identity providers.
     */
    public IdentityProviderList build(String language) {
        try {
            URI uri = new URI(idpUrl);

            logger.info("Listing identity providers from {}", uri);
            String response = RestClient.create(uri)
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam("lang", language).build())
                .retrieve()
                .body(String.class);

            String json = response.replaceAll("\\<.*?>", ""); // strip possible html
            logger.info("Identity provider list: {}", json);

            return parse(json);
        } catch (Exception e) {
            throw new OidcDemoException("Building the list of identity providers failed!", e);
        }
    }

    static IdentityProviderList parse(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, IdentityProviderList.class);
    }
}
