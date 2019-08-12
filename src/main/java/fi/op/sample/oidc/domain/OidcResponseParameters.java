package fi.op.sample.oidc.domain;

/**
 * Response after user has completed authentication flow with his or her browser.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class OidcResponseParameters {
    // Error code.
    private String error;
    // Authorization code. In successful identification we exchange the authorization code for identity token.
    private String code;
    // Black box where you can store anything you want and it is returned untouched after the id flow.
    private String state;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
