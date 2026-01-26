// SPDX-FileCopyrightText: Copyright 2026 OP Pohjola (https://op.fi)
//
// SPDX-License-Identifier: MIT

package fi.op.sample.oidc.domain;

public class Configuration {
    public static final String KEYSTORE_LOCATION = "saippuakauppias.jks";

    public static final String CLIENT_ID = "saippuakauppias";
    public static final String FTN_SPNAME = "Soap for the people";
    public static final String RESPONSE_TYPE = "code";
    public static final String AUTHORIZE_URL = "https://isb-test.op.fi/oauth/authorize";
    public static final String JWKS_PROXY = "https://isb-test.op.fi/jwks/broker-signed";
    public static final String TOKEN_URL = "https://isb-test.op.fi/oauth/token";
    public static final String TOKEN_PROXY = "https://isb-test.op.fi/oauth/token";
    public static final String IDP_LIST_URL = "https://isb-test.op.fi/api/embedded-ui/";
    public static final String PROMPT = "auto";
    public static final String REDIRECT_URI = "http://localhost:8080/finishFlow";
    public static final String SP_HOST = "http://localhost:8080";
    public static final String SCOPE = "openid profile personal_identity_code";
    public static final String ISB_PUB_SIGNING_KEY_PEM_FILE = "./src/main/resources/sandbox-isb-entity-signing-pubkey.pem";

    private Configuration() {
        // Does nothing but required by SonarQube
    }
}
