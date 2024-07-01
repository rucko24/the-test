package org.example.util;

import java.util.EnumSet;
import java.util.Locale;

/**
 * Get operating system in runtime
 */
public enum GetOperatingSystem {

    WINDOWS("window"),
    LINUX("linux"),
    FREEBSD("freebsd"),
    MAC("mac os"),
    OTHER("other");

    private final String osName;

    GetOperatingSystem(final String osName) {
        this.osName = osName;
    }

    public String getOsName() {
        return osName;
    }

    public static GetOperatingSystem getOsInfo() {
        final String currentOs = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        return EnumSet.allOf(GetOperatingSystem.class)
                .stream()
                .filter(osEnum -> currentOs.contains(osEnum.getOsName()))
                .findFirst()
                .orElse(OTHER);
    }

}
