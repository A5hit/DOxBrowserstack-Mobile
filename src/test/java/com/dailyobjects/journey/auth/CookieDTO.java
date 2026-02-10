package com.dailyobjects.journey.auth;

public class CookieDTO {
    public String name;
    public String value;
    public String domain;
    public String path;
    public boolean secure;
    public boolean httpOnly;
    public Long expiryEpochSec; // nullable
}
