// SPDX-FileCopyrightText: Copyright 2026 OP Pohjola (https://op.fi)
//
// SPDX-License-Identifier: MIT

package fi.op.sample.oidc.domain;

public class OidcDemoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public OidcDemoException(String message) {
        super(message);
    }

    public OidcDemoException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
