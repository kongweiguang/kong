package io.github.kongweiguang.http.client.consts;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpConstantsContractTest {

    @Test
    void contentTypeExposesCanonicalValueAndCompatibilityAccessor() {
        assertEquals("application/json", ContentType.JSON.value());
        assertEquals(ContentType.JSON.value(), ContentType.JSON.toString());

        assertEquals("application/javascript", ContentType.APPLICATION_JAVASCRIPT.value());
    }

    @Test
    void headerExposesCanonicalValueAndCompatibilityAccessor() {
        assertEquals("Content-Type", Header.CONTENT_TYPE.value());
        assertEquals(Header.CONTENT_TYPE.value(), Header.CONTENT_TYPE.toString());

        assertEquals("If-None-Match", Header.IF_NONE_MATCH.value());
        assertEquals("Accept-Ranges", Header.ACCEPT_RANGES.value());
    }

    @Test
    void methodProvidesExplicitHttpTokenMetadata() {
        assertEquals("GET", Method.GET.value());
        assertEquals("PATCH", Method.PATCH.value());
        assertFalse(Method.GET.permitsRequestBody());
        assertTrue(Method.POST.permitsRequestBody());
    }

    @Test
    void constProvidesOnlyCanonicalNames() {
        assertEquals("http", Const.HTTP);
        assertEquals("https://", Const.HTTPS_PREFIX);
        assertEquals("localhost", Const.LOCALHOST);
    }

    @Test
    void userAgentProvidesStandardAccessorAndReadableString() {
        assertEquals(UserAgent.Windows.CHROME.value(), UserAgent.Windows.CHROME.toString());
        assertEquals(UserAgent.MacOS.FIREFOX.value(), UserAgent.MacOS.FIREFOX.toString());
    }
}
