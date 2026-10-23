package com.ecommerce.commonlib.web.filter;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class SensitiveDataMaskerTest {

    @Test
    void masksPasswordInJsonBody() {
        assertEquals("{\"username\":\"kim\",\"password\":\"***\"}",
                SensitiveDataMasker.mask("{\"username\":\"kim\",\"password\":\"Passw0rd1\"}"));
    }

    @Test
    void masksTokensAndEscapedQuotes() {
        assertEquals("{\"access_token\":\"***\", \"refreshToken\" : \"***\"}",
                SensitiveDataMasker.mask("{\"access_token\":\"eyJ.a.b\", \"refreshToken\" : \"r\\\"x\"}"));
    }

    @Test
    void keepsApiResponseErrorCode() {
        String body = "{\"success\":false,\"code\":\"ERR-0409\",\"message\":\"sold out\"}";
        assertEquals(body, SensitiveDataMasker.mask(body));
    }

    @Test
    void masksOAuthQueryParameters() {
        assertEquals("code=***&state=***&session_state=abc",
                SensitiveDataMasker.mask("code=SplxlOBeZQQYbYS6WxSbIA&state=xyz&session_state=abc"));
        assertEquals("ticket=***", SensitiveDataMasker.mask("ticket=abc123"));
    }

    @Test
    void masksFormEncodedPassword() {
        assertEquals("grant_type=password&username=kim&password=***",
                SensitiveDataMasker.mask("grant_type=password&username=kim&password=secret"));
    }

    @Test
    void leavesOrdinaryTextAlone() {
        assertEquals("{\"productTitle\":\"phone\",\"quantity\":3}",
                SensitiveDataMasker.mask("{\"productTitle\":\"phone\",\"quantity\":3}"));
        assertNull(SensitiveDataMasker.mask(null));
    }
}
