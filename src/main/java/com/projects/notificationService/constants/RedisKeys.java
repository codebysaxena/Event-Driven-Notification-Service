package com.projects.notificationService.constants;

public class RedisKeys {
    private RedisKeys() {}

    public static final String BLACKLIST = "blacklist:";
    public static final String RATELIMIT = "rateLimit:";
    public static final int MAXLIMIT = 10;
}
