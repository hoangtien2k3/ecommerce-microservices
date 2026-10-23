package com.ecommerce.commonlib.web.filter;

import java.util.regex.Pattern;

/**
 * Hides credentials before a request or response is written to the access log:
 * JSON fields ({@code "password":"..."}), form or query parameters ({@code password=...}).
 */
final class SensitiveDataMasker {

    private static final String MASK = "***";
    private static final String CREDENTIAL_KEYS =
            "password|newPassword|oldPassword|confirmPassword"
            + "|access_token|accessToken|refresh_token|refreshToken|id_token|idToken|token"
            + "|client_secret|clientSecret|ticket";
    // OAuth code/state only travel as URL parameters; in JSON "code" is ApiResponse's error code.
    private static final String PARAM_KEYS = CREDENTIAL_KEYS + "|code|state";

    private static final Pattern JSON_FIELD = Pattern.compile(
            "(\"(?:" + CREDENTIAL_KEYS + ")\"\\s*:\\s*)\"(?:[^\"\\\\]|\\\\.)*\"", Pattern.CASE_INSENSITIVE);
    private static final Pattern PARAM = Pattern.compile(
            "(?<=^|[?&\\s])((?:" + PARAM_KEYS + ")=)[^&\\s]*", Pattern.CASE_INSENSITIVE);

    private SensitiveDataMasker() {
    }

    static String mask(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        String masked = JSON_FIELD.matcher(text).replaceAll("$1\"" + MASK + "\"");
        return PARAM.matcher(masked).replaceAll("$1" + MASK);
    }
}
