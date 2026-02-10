package com.dailyobjects.journey.auth;

import java.util.Map;
import java.util.List;

public class AuthSession {
    public String baseUrl;
    public long createdAtEpochMs;
    public Long accessTokenExpEpochSec; // optional
    public List<CookieDTO> cookies;
    public Map<String, String> localStorage;
}
