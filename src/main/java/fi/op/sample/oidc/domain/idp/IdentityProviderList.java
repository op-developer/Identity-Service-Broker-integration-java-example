// SPDX-FileCopyrightText: Copyright 2026 OP Pohjola (https://op.fi)
//
// SPDX-License-Identifier: MIT

package fi.op.sample.oidc.domain.idp;

import java.util.List;
import java.util.Map;

public class IdentityProviderList {
    private List<IdentityProvider> identityProviders;
    private String isbProviderInfo;
    private String isbConsent;
    private String privacyNoticeLink;
    private String privacyNoticeText;
    private Map<String, Object> disturbanceInfo;

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

    public void setDisturbanceInfo(Map<String, Object> disturbanceInfo) {
        this.disturbanceInfo = disturbanceInfo;
    }

    public Map<String, Object> getDisturbanceInfo() {
        return disturbanceInfo;
    }
}
