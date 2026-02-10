package com.dailyobjects.journey.auth;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

public final class AuthSessionApplier {
    private AuthSessionApplier(){}

    public static void apply(WebDriver driver, AuthSession session) {
        String baseUrl = session.baseUrl;
        String host = URI.create(baseUrl).getHost();

        // Set origin context
        driver.get(baseUrl);

        // Clear state
        driver.manage().deleteAllCookies();
        if (supportsWebStorage(driver)) {
            safeExec(driver, "try { window.localStorage.clear(); } catch (e) { /* ignore */ }");
        }

        // localStorage first (mandatory for your Cognito/Amplify auth)
        if (session.localStorage != null && supportsWebStorage(driver)) {
            for (Map.Entry<String, String> e : session.localStorage.entrySet()) {
                safeExec(driver,
                        "try { window.localStorage.setItem(arguments[0], arguments[1]); } catch (e) { /* ignore */ }",
                        e.getKey(), e.getValue()
                );
            }
        }

        // cookies next (optional)
        if (session.cookies != null) {
            for (CookieDTO c : session.cookies) {
                if (!cookieDomainMatches(host, c.domain)) continue;

                Cookie.Builder b = new Cookie.Builder(c.name, c.value)
                        .path((c.path == null || c.path.isBlank()) ? "/" : c.path);

                // safest: avoid setting domain unless you must
                // setting an incompatible domain can throw InvalidCookieDomainException
                if (c.domain != null && !c.domain.isBlank()) {
                    String normalized = c.domain.startsWith(".") ? c.domain.substring(1) : c.domain;
                    // only set if host endsWith domain
                    if (host.endsWith(normalized)) b.domain(normalized);
                }

                if (c.secure) b.isSecure(true);
                if (c.httpOnly) b.isHttpOnly(true);
                if (c.expiryEpochSec != null) b.expiresOn(java.util.Date.from(Instant.ofEpochSecond(c.expiryEpochSec)));

                try {
                    driver.manage().addCookie(b.build());
                } catch (Exception ignore) {
                    // ignore cookies that browser refuses
                }
            }
        }

        driver.navigate().refresh();
    }

    private static void safeExec(WebDriver driver, String script, Object... args) {
        try {
            ((JavascriptExecutor) driver).executeScript(script, args);
        } catch (Exception ignored) {
            // some browsers (notably iOS Safari on BrowserStack) may block storage access
        }
    }

    private static boolean supportsWebStorage(WebDriver driver) {
        if (driver instanceof RemoteWebDriver) {
            Object cap = ((RemoteWebDriver) driver).getCapabilities().getCapability("webStorageEnabled");
            if (cap instanceof Boolean) return (Boolean) cap;
        }
        return true;
    }

    private static boolean cookieDomainMatches(String host, String cookieDomain) {
        if (cookieDomain == null || cookieDomain.isBlank()) return true;
        String cd = cookieDomain.startsWith(".") ? cookieDomain.substring(1) : cookieDomain;
        return host.endsWith(cd);
    }
}
