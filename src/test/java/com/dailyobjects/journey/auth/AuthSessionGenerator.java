package com.dailyobjects.journey.auth;

import com.dailyobjects.journey.config.ConfigReader;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.URI;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public final class AuthSessionGenerator {

    public AuthSession generateFromLocalChromeProfile(String baseUrl) {
        ChromeOptions options = new ChromeOptions();

        // Use your existing config knobs
        // PROFILE_PATH should point to Chrome user-data-dir
        // PROFILE can be "Default" / "Profile 1" etc, OR "true" if you only want user-data-dir.
        String profilePath = ConfigReader.PROFILE_PATH;
        String profile = ConfigReader.PROFILE;

        boolean useProfile = profile != null && !profile.isBlank() && !profile.equalsIgnoreCase("false");
        if (useProfile && profilePath != null && !profilePath.isBlank()) {
            options.addArguments("--user-data-dir=" + profilePath);
        }
        if (useProfile && profile != null && !profile.isBlank() && !profile.equalsIgnoreCase("true")) {
            options.addArguments("--profile-directory=" + profile);
        }

        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");

        WebDriver driver = new ChromeDriver(options);
        try {
            driver.get(baseUrl);

            Map<String, String> authLS = dumpAuthLocalStorage(driver);
            String accessToken = pickAnyCognitoAccessToken(authLS);

            if (accessToken == null) {
                throw new RuntimeException(
                        "No Cognito accessToken found in localStorage for " + baseUrl +
                                ". Open this URL in the same Chrome profile, login once, then rerun."
                );
            }

            Set<Cookie> cookies = driver.manage().getCookies();
            AuthSession s = new AuthSession();
            s.baseUrl = baseUrl;
            s.createdAtEpochMs = System.currentTimeMillis();
            s.localStorage = authLS;
            s.cookies = cookies.stream().map(AuthSessionGenerator::toDto).collect(Collectors.toList());
            s.accessTokenExpEpochSec = JwtUtil.tryGetExpEpochSec(accessToken);

            return s;
        } finally {
            driver.quit();
        }
    }

    private static CookieDTO toDto(Cookie c) {
        CookieDTO d = new CookieDTO();
        d.name = c.getName();
        d.value = c.getValue();
        d.domain = c.getDomain();
        d.path = c.getPath();
        d.secure = c.isSecure();
        d.httpOnly = c.isHttpOnly();
        d.expiryEpochSec = (c.getExpiry() != null) ? c.getExpiry().toInstant().getEpochSecond() : null;
        return d;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, String> dumpAuthLocalStorage(WebDriver driver) {
        Map<String, String> all = (Map<String, String>) ((JavascriptExecutor) driver).executeScript(
                "var out = {}; " +
                        "for (var i=0; i<localStorage.length; i++) {" +
                        "  var k = localStorage.key(i);" +
                        "  out[k] = localStorage.getItem(k);" +
                        "} return out;"
        );

        Map<String, String> auth = new HashMap<>();
        if (all == null) return auth;

        for (var e : all.entrySet()) {
            String k = e.getKey();
            if (k == null) continue;
            if (k.startsWith("CognitoIdentityServiceProvider.") || k.equals("amplify-signin-with-hostedUI")) {
                auth.put(k, e.getValue());
            }
        }
        return auth;
    }

    private static String pickAnyCognitoAccessToken(Map<String, String> authLS) {
        if (authLS == null) return null;
        for (var e : authLS.entrySet()) {
            if (e.getKey().endsWith(".accessToken") && e.getValue() != null && e.getValue().contains(".")) {
                return e.getValue();
            }
        }
        return null;
    }
}
