package com.reynaud.poseuralert.util;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

public final class UidGenerator {
    private UidGenerator() {}

    public static String generateDeterministicFromEmail(String email) {
        if (email == null) {
            return generateRandom();
        }
        String norm = "poseuralert:" + email.trim().toLowerCase(Locale.ROOT);
        return UUID.nameUUIDFromBytes(norm.getBytes(StandardCharsets.UTF_8)).toString();
    }

    public static String generateRandom() {
        return UUID.randomUUID().toString();
    }
}
