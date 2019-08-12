package fi.op.sample.oidc.domain.idp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An identity provider.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class IdentityProvider {
    private String name;
    private String imageUrl;
    private String ftnIdpId;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @JsonProperty("ftn_idp_id")
    public void setFtnIdpId(String ftnIdpId) {
        this.ftnIdpId = ftnIdpId;
    }

    public String getFtnIdpId() {
        return ftnIdpId;
    }
}
