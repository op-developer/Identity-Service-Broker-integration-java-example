package fi.op.sample.oidc.domain;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fi.op.sample.oidc.domain.idp.IdentityProvider;
import fi.op.sample.oidc.domain.idp.IdentityProviderList;

/**
 *
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut
 * @version 1.0
 */
public class IdentityProviderListBuilderTest {
    @Test
    public void testIdentityProviderListBuilder() {
        IdentityProviderListBuilder sut = new IdentityProviderListBuilder();
        IdentityProviderList result = sut.build("en");
        Assert.assertTrue(result.getIdentityProviders().size() > 0);
    }

    @Test
    public void testIdentityProviderListParsing() throws IOException {
        final String testData = "{\"identityProviders\":[{\"name\":\"Aktia\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/aktia.svg\",\"ftn_idp_id\":\"aktia\"},{\"name\":\"Danske Bank\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/danske-bank.svg\",\"ftn_idp_id\":\"danskebank\"},{\"name\":\"Handelsbanken\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/handelsbanken.svg\",\"ftn_idp_id\":\"handelsbanken\"},{\"name\":\"Testi Tupas\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/tupas-mock.svg\",\"ftn_idp_id\":\"mocktupas\"},{\"name\":\"Nordea\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/nordea.svg\",\"ftn_idp_id\":\"nordea\"},{\"name\":\"Testi OIDC\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/oidc-mock.svg\",\"ftn_idp_id\":\"oidcTest\"},{\"name\":\"OP\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/op.svg\",\"ftn_idp_id\":\"op-saml2\"},{\"name\":\"Oma Säästöpankki\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/omasp.svg\",\"ftn_idp_id\":\"omasp\"},{\"name\":\"OP\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/op.svg\",\"ftn_idp_id\":\"op\"},{\"name\":\"POP Pankki\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/pop-pankki.svg\",\"ftn_idp_id\":\"poppankki\"},{\"name\":\"S-Pankki\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/s-pankki.svg\",\"ftn_idp_id\":\"spankki\"},{\"name\":\"Säästöpankki\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/saastopankki.svg\",\"ftn_idp_id\":\"saastopankki\"},{\"name\":\"Testi Saml2\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/saml2-mock.svg\",\"ftn_idp_id\":\"testidp\"},{\"name\":\"Ålandsbanken\",\"imageUrl\":\"https://isb.idbroker-dev.aws.op-palvelut.net/public/images/idp/alandsbanken.svg\",\"ftn_idp_id\":\"alandsbanken\"}],\"isbProviderInfo\":\"OP Tunnistuksen välityspalvelun tarjoaa OP Ryhmän osuuspankit ja OP Yrityspankki Oyj.\",\"isbConsent\":\"Tunnistautumalla seuraavilla tunnistustavoilla hyväksyt, että palveluntarjoajalle välitetään: henkilötunnus, nimi.\",\"privacyNoticeLink\":\"https://isb-test.op.fi/privacy-info\",\"privacyNoticeText\":\"OP tietosuojaseloste\"}";
        IdentityProviderList result = IdentityProviderListBuilder.parse(testData);
        List<IdentityProvider> idps = result.getIdentityProviders();
        Assert.assertEquals(idps.size(), 14);
        Assert.assertTrue(idps.get(0).getFtnIdpId().length() > 0);
        Assert.assertTrue(idps.get(0).getImageUrl().length() > 0);
        Assert.assertTrue(idps.get(0).getName().length() > 0);
    }
}
