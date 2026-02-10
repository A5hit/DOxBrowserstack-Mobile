package com.dailyobjects.journey.auth;

public final class AuthSessionManager {
    private AuthSessionManager(){}

    public static synchronized AuthSession ensureValid(String baseUrl) {
        AuthSession s = AuthSessionStore.loadOrNull();
        if (s != null && baseUrl.equals(s.baseUrl)) {
            return s;
        }
        throw new RuntimeException(
                "Auth session is missing or baseUrl mismatch for: " + baseUrl + "\n" +
                        "Manual reseed required:\n" +
                        "1) Login once in Chrome using your desired profile\n" +
                        "2) Run AuthSeedTest (or AuthSessionGenerator) to write target/auth-session.json\n" +
                        "3) Re-run the suite"
        );
    }
}
