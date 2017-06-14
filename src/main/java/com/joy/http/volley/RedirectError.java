package com.joy.http.volley;

import com.joy.http.JoyError;

/**
 * Indicates that there was a redirection.
 */
public class RedirectError extends JoyError {

    public RedirectError() {
        super();
    }

    public RedirectError(int type, int statusCode, Throwable cause) {
        super(type, statusCode, cause);
    }
}
