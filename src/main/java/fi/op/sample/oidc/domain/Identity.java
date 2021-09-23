package fi.op.sample.oidc.domain;

import java.io.Serializable;


/**
 * User's authenticated identity.
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class Identity implements Serializable {
    private static final long serialVersionUID = 1L;

    // Social security number
    private String ssn;
    // First & maybe middle / last name.
    private String name;
    private String identityRawData;

    public Identity() {
        // Does nothing but required by SonarQube.
    }

    public Identity(String ssn, String name) {
        this.ssn = ssn;
        this.name = name;
    }

    public String getSsn() {
        return ssn;
    }

    public void setSsn(String ssn) {
        this.ssn = ssn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



	public String getIdentityRawData() {
		return identityRawData;
	}

	public void setIdentityRawData(String identityRawData) {
		this.identityRawData = identityRawData;
	}
}
