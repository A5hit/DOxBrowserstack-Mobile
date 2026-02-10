package com.dailyobjects.journey.auth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JwtUtil {
    private JwtUtil(){}

    public static Long tryGetExpEpochSec(String jwt) {
        try {
            if (jwt == null || !jwt.contains(".")) return null;
            String payload = jwt.split("\\.")[1];
            byte[] decoded = Base64.getUrlDecoder().decode(payload);
            String json = new String(decoded, StandardCharsets.UTF_8);

            // tiny parse (no full JSON parser required):
            // looks for: "exp":1234567890
            int idx = json.indexOf("\"exp\"");
            if (idx < 0) return null;
            int colon = json.indexOf(':', idx);
            if (colon < 0) return null;

            int end = colon + 1;
            while (end < json.length() && Character.isWhitespace(json.charAt(end))) end++;

            int start = end;
            while (end < json.length() && Character.isDigit(json.charAt(end))) end++;

            return Long.parseLong(json.substring(start, end));
        } catch (Exception e) {
            return null;
        }
    }
}
