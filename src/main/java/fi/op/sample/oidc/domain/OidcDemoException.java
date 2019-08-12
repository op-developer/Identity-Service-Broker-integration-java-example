package fi.op.sample.oidc.domain;

/**
 * Exception
 *
 * <p>
 * Copyright (c) 2019 OP-Services Ltd.
 * </p>
 *
 * @author OP-Palvelut Oy
 * @license http://www.gnu.org/copyleft/gpl.html GNU General Public License
 * @version 1.0
 */
public class OidcDemoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public OidcDemoException(String message) {
        super(message);
    }

    public OidcDemoException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
