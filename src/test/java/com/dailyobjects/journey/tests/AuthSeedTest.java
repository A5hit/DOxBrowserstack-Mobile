package com.dailyobjects.journey.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class AuthSeedTest {

    // ========= Runtime inputs (pass via -D...) =========
    private final String baseUrl = System.getProperty("baseUrl", "https://dev.marche.dailyobjects.com/");
    private final String chromeUserDataDir = System.getProperty("chromeUserDataDir", "");     // REQUIRED
    private final String chromeProfileDir = System.getProperty("chromeProfileDir", "Default"); // Default / Profile 1 / ...
    private final String outFile = System.getProperty("authOutFile", "target/auth-session.json");

    // ========= LOGIN VALIDATION STRATEGY =========
    // Validate by URL behavior:
    // Logged-in: BaseURL/map stays on /map
    // Logged-out: BaseURL/map redirects to /ap (or other auth page)

    private static final ObjectMapper OM = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    @Test
    public void seedAuthStateToJson() throws Exception {
        assertConfig();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--user-data-dir=" + chromeUserDataDir);
        options.addArguments("--profile-directory=" + chromeProfileDir);
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        // Stability flags for Windows profile launches
        options.addArguments("--disable-extensions");
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");
        options.addArguments("--disable-gpu");
        options.addArguments("--remote-debugging-port=0");

        WebDriver driver = new ChromeDriver(options);

        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(60));

            // Step 3: open domain and ensure you're logged in (marker-based)
            driver.get(baseUrl);
            ensureLoggedIn(driver, wait);

            // Export auth state (localStorage + cookies)
            Map<String, String> authLocalStorage = dumpAuthLocalStorage(driver);
            List<CookieDTO> cookies = dumpCookies(driver);

            AuthSession session = new AuthSession();
            session.baseUrl = baseUrl;
            session.createdAtEpochMs = System.currentTimeMillis();
            session.localStorage = authLocalStorage;
            session.cookies = cookies;
            session.accessTokenExpEpochSec = tryDetectCognitoAccessTokenExp(authLocalStorage, cookies);

            // Write file
            Path p = Path.of(outFile);
            Path parent = p.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            OM.writeValue(p.toFile(), session);

            System.out.println("✅ Auth session saved to: " + p.toAbsolutePath());
            if (session.accessTokenExpEpochSec != null) {
                System.out.println("   accessToken exp epoch: " + session.accessTokenExpEpochSec);
            }
            System.out.println("   localStorage auth keys captured: " + session.localStorage.size());
            System.out.println("   cookies captured: " + session.cookies.size());

            // Step 4: validate file content (required keys present)
            try {
                AuthSession loaded = OM.readValue(new File(outFile), AuthSession.class);
                assertAuthSessionLooksValid(loaded);
            } catch (Exception e) {
                Assert.fail("❌ Auth session JSON read/validate failed. File: " + outFile, e);
            }

        } finally {
            driver.quit();
        }
    }

    private void assertConfig() {
        Assert.assertTrue(baseUrl.startsWith("https://"), "baseUrl must start with https://");
        Assert.assertFalse(chromeUserDataDir.isBlank(),
                "Missing -DchromeUserDataDir.\n" +
                        "Windows example: C:\\Users\\<you>\\AppData\\Local\\Google\\Chrome\\User Data\n" +
                        "macOS example: /Users/<you>/Library/Application Support/Google/Chrome");
    }

    private void ensureLoggedIn(WebDriver driver, WebDriverWait wait) {
        String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String mapUrl = base + "/map";
        String apUrl = base + "/ap";

        driver.get(mapUrl);
        wait.until(d -> d.getCurrentUrl() != null &&
                (d.getCurrentUrl().startsWith(mapUrl) || d.getCurrentUrl().startsWith(apUrl)));

        String current = driver.getCurrentUrl();
        if (current.startsWith(apUrl)) {
            throw new RuntimeException(
                    "Not logged in for baseUrl in this Chrome profile.\n" +
                            "Redirected to: " + current + "\n" +
                            "Fix:\n" +
                            "1) Open " + baseUrl + " in the SAME Chrome profile,\n" +
                            "2) Login once,\n" +
                            "3) Close Chrome completely,\n" +
                            "4) Rerun AuthSeedTest."
            );
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> dumpAuthLocalStorage(WebDriver driver) {
        Object raw = ((JavascriptExecutor) driver).executeScript(
                "var out = {}; " +
                        "for (var i=0; i<localStorage.length; i++) {" +
                        "  var k = localStorage.key(i);" +
                        "  out[k] = localStorage.getItem(k);" +
                        "} return out;"
        );

        Map<String, Object> all = (raw instanceof Map) ? (Map<String, Object>) raw : new HashMap<>();
        Map<String, String> auth = new HashMap<>();

        for (Map.Entry<String, Object> e : all.entrySet()) {
            String k = e.getKey();
            if (k == null) continue;
            auth.put(k, Objects.toString(e.getValue(), null));
        }
        return auth;
    }

    private List<CookieDTO> dumpCookies(WebDriver driver) {
        Set<org.openqa.selenium.Cookie> cookies = driver.manage().getCookies();
        return cookies.stream().map(c -> {
            CookieDTO d = new CookieDTO();
            d.name = c.getName();
            d.value = c.getValue();
            d.domain = c.getDomain();
            d.path = c.getPath();
            d.secure = c.isSecure();
            d.httpOnly = c.isHttpOnly();
            d.expiryEpochSec = (c.getExpiry() != null) ? c.getExpiry().toInstant().getEpochSecond() : null;
            return d;
        }).collect(Collectors.toList());
    }

    private Long tryDetectCognitoAccessTokenExp(Map<String, String> authLS, List<CookieDTO> cookies) {
        if (authLS != null) {
            // pick any *.accessToken from Cognito localStorage
            for (Map.Entry<String, String> e : authLS.entrySet()) {
                if (e.getKey().endsWith(".accessToken")) {
                    Long exp = tryGetJwtExp(e.getValue());
                    if (exp != null) return exp;
                }
            }
        }

        if (cookies != null) {
            for (CookieDTO c : cookies) {
                if (c == null || c.name == null || c.value == null) continue;

                boolean tokenLikeName = c.name.endsWith(".accessToken") || c.name.equalsIgnoreCase("token");
                if (!tokenLikeName) continue;

                Long exp = tryGetJwtExp(c.value);
                if (exp != null) return exp;
            }
        }

        return null;
    }

    private Long tryGetJwtExp(String jwt) {
        try {
            if (jwt == null || !jwt.contains(".")) return null;
            String payload = jwt.split("\\.")[1];
            byte[] decoded = Base64.getUrlDecoder().decode(payload);
            String json = new String(decoded, StandardCharsets.UTF_8);

            int idx = json.indexOf("\"exp\"");
            if (idx < 0) return null;
            int colon = json.indexOf(':', idx);
            if (colon < 0) return null;

            int i = colon + 1;
            while (i < json.length() && Character.isWhitespace(json.charAt(i))) i++;

            int start = i;
            while (i < json.length() && Character.isDigit(json.charAt(i))) i++;

            return Long.parseLong(json.substring(start, i));
        } catch (Exception ex) {
            return null;
        }
    }

    private void assertAuthSessionLooksValid(AuthSession s) {
        Assert.assertNotNull(s, "auth session json could not be read");
        Assert.assertEquals(s.baseUrl, baseUrl, "baseUrl mismatch in auth session");
        Assert.assertNotNull(s.localStorage, "localStorage missing in auth session json");
        Assert.assertTrue(
                (s.localStorage != null && !s.localStorage.isEmpty()) ||
                        (s.cookies != null && !s.cookies.isEmpty()),
                "Both localStorage and cookies are empty; auth artifacts were not captured"
        );

        if (s.accessTokenExpEpochSec != null) {
            long now = Instant.now().getEpochSecond();
            Assert.assertTrue(s.accessTokenExpEpochSec > now,
                    "Captured accessToken already expired (exp=" + s.accessTokenExpEpochSec + ", now=" + now + "). Re-login and reseed.");
        }

        System.out.println("✅ Auth session validation passed.");
    }

    private String safe(SupplierWithException<String> s) {
        try { return s.get(); } catch (Exception e) { return "<unavailable>"; }
    }

    @FunctionalInterface
    interface SupplierWithException<T> { T get() throws Exception; }

    // ========= JSON Models =========
    public static class AuthSession {
        public String baseUrl;
        public long createdAtEpochMs;
        public Long accessTokenExpEpochSec;
        public List<CookieDTO> cookies;
        public Map<String, String> localStorage;
    }

    public static class CookieDTO {
        public String name;
        public String value;
        public String domain;
        public String path;
        public boolean secure;
        public boolean httpOnly;
        public Long expiryEpochSec;
    }
}
