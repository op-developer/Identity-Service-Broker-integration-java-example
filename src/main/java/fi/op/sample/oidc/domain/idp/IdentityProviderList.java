package fi.op.sample.oidc.domain.idp;

import java.util.List;

import net.minidev.json.JSONObject;

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
    private String privacyNoticeLink;
    private String privacyNoticeText;
    private JSONObject disturbanceInfo;

    // "isbProviderInfo":"OP Tunnistuksen välityspalvelun tarjoaa OP Ryhmän osuuspankit ja OP Yrityspankki Oyj.",
    // "isbConsent":"Tunnistautumalla seuraavilla tunnistustavoilla hyväksyt, että palveluntarjoajalle välitetään:
    // henkilötunnus, nimi.",
    // "disturbanceInfo": {"header":"Häiriöilmoitus","text":"Tässä on häiriöilmoituksen tilavaraus toteutuksen helpottamiseksi"}

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

    public void setPrivacyNoticeLink(String privacyNoticeLink) {
        this.privacyNoticeLink = privacyNoticeLink;
    }

    public String getPrivacyNoticeLink() {
        return privacyNoticeLink;
    }

    public void setPrivacyNoticeText(String privacyNoticeText) {
        this.privacyNoticeText = privacyNoticeText;
    }

    public String getPrivacyNoticeText() {
        return privacyNoticeText;
    }

    public void setDisturbanceInfo(JSONObject disturbanceInfo) {
        this.disturbanceInfo = disturbanceInfo;
    }

    public JSONObject getDisturbanceInfo() {
        return disturbanceInfo;
    }
}
