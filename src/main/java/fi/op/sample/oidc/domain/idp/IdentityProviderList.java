package fi.op.sample.oidc.domain.idp;

import java.util.List;

/**
 * A list of identity providers.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy (OP Services Ltd)
 * @version 1.0
 */
public class IdentityProviderList {
    private List<IdentityProvider> identityProviders;
    private String isbProviderInfo;
    private String isbConsent;
    private String isbIconUrl;

    // "isbProviderInfo":"OP Tunnistuksen välityspalvelun tarjoaa OP Ryhmän osuuspankit ja OP Yrityspankki Oyj.",
    // "isbConsent":"Tunnistautumalla seuraavilla tunnistustavoilla hyväksyt, että palveluntarjoajalle välitetään:
    // henkilötunnus, nimi.",
    // "isbIconUrl":"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/checkout.svg"

    public List<IdentityProvider> getIdentityProviders() {
        return identityProviders;
    }

    public void setIdentityProviders(List<IdentityProvider> identityProviders) {
        this.identityProviders = identityProviders;
    }

    public void setIsbProviderInfo(String isbProviderInfo) {
        this.isbProviderInfo = isbProviderInfo;
    }

    public String getIsbProviderInfo() {
        return isbProviderInfo;
    }

    public void setIsbConsent(String isbConsent) {
        this.isbConsent = isbConsent;
    }

    public String getIsbConsent() {
        return isbConsent;
    }

    public void setIsbIconUrl(String isbIconUrl) {
        this.isbProviderInfo = isbIconUrl;
    }

    public String getIsbIconUrl() {
        return isbIconUrl;
    }
}
