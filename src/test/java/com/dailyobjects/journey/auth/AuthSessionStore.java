package com.dailyobjects.journey.auth;

import browserstack.shaded.jackson.databind.ObjectMapper;
import browserstack.shaded.jackson.databind.SerializationFeature;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;

public class AuthSessionStore {
    private static final ObjectMapper OM = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final Path FILE = Path.of("target", "auth-session.json");

    private AuthSessionStore(){}

    public static AuthSession loadOrNull() {
        try {
            if (!Files.exists(FILE)) return null;
            return OM.readValue(FILE.toFile(), AuthSession.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static void save(AuthSession session) {
        try {
            Files.createDirectories(FILE.getParent());
            OM.writeValue(FILE.toFile(), session);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save auth session: " + e.getMessage(), e);
        }
    }

    public static boolean isValid(AuthSession s, int skewSeconds) {
        if (s == null) return false;
        long nowSec = Instant.now().getEpochSecond();
        if (s.accessTokenExpEpochSec != null) {
            return (s.accessTokenExpEpochSec - skewSeconds) > nowSec;
        }

        // Fallback: if no accessToken exp is available, treat session as valid for a fixed TTL
        long maxAgeSec = 6 * 60 * 60; // 6 hours
        long createdSec = s.createdAtEpochMs / 1000;
        return (createdSec + maxAgeSec) > nowSec;
    }
}
