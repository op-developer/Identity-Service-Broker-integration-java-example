// SPDX-FileCopyrightText: Copyright 2026 OP Pohjola (https://op.fi)
//
// SPDX-License-Identifier: MIT

package fi.op.sample.oidc.domain.idp;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An identity provider.
 *
 * @author OP-Palvelut Oy
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
