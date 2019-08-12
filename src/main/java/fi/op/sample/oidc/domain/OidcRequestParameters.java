package fi.op.sample.oidc.domain;

import java.io.Serializable;

/**
 * Transfer object of OIDC settings.
 * 
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class OidcRequestParameters implements Serializable {

    private static final long serialVersionUID = 2183778571888171940L;

    // Authorization endpoint where user's browser starts the identification flow.
    private String endpointUrl;

    // OIDC client id.
    private String clientId;

    // URL where user's browser returns after identification flow.
    private String redirectUri;

    private String responseType;

    // OIDC scope, determines purpose (identification, creating weak credentials, chaining strong credentials) and what
    // the client wants.
    private String scope;

    // UI language.
    private String uiLocales;

    private String nonce;

    // "consent" means that ui should explicitly ask the user permission to forward his identity.
    private String prompt;

    // A black box where you can store anything you want and it is returned untouched after identification flow.
    private String state;

    // Identity provider's id. Can be empty. If empty, the broker shows the user a list of identity providers.
    private String ftnIdpId;

    // Authentication request as a signed Java Web Token.
    private String request;

    // URL from which we get the broker's public keys.
    private String jwksProxy;

    // OIDC token exchange URL for exchaging authorization code into id token.
    // We don't have direct access here.
    // We put this to JWT's "audience" field and send the request through proxy.
    private String tokenUrl;

    // Apigee endpoint, which proxies to tokenUrl.
    private String tokenProxy;

    public String getEndpointUrl() {
        return endpointUrl;
    }

    public void setEndpointUrl(String endpointUrl) {
        this.endpointUrl = endpointUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUiLocales() {
        return uiLocales;
    }

    public void setUiLocales(String uiLocales) {
        this.uiLocales = uiLocales;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFtnIdpId() {
        return ftnIdpId;
    }

    public void setFtnIdpId(String ftnIdpId) {
        this.ftnIdpId = ftnIdpId;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getJwksProxy() {
        return this.jwksProxy;
    }

    public void setJwksProxy(String jwksProxy) {
        this.jwksProxy = jwksProxy;
    }

    public String getTokenUrl() {
        return this.tokenUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public String getTokenProxy() {
        return this.tokenProxy;
    }

    public void setTokenProxy(String tokenProxy) {
        this.tokenProxy = tokenProxy;
    }
}
