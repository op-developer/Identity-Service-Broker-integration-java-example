package fi.op.sample.oidc.domain;

/**
 * Configuration, except for passwords and secret keys which should be placed in a HSM.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
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
