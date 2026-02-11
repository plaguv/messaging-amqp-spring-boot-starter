package io.github.plaguv.contract.event;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record EventVersion(
        int major,
        int minor,
        int patch
) {
    public EventVersion {
        if (major < 0) {
            throw new IllegalArgumentException("EventVersion attribute 'major' cannot be negative, was '%d'".formatted(major));
        }
        if (minor < 0) {
            throw new IllegalArgumentException("EventVersion attribute 'minor' cannot be negative, was '%d'".formatted(minor));
        }
        if (patch < 0) {
            throw new IllegalArgumentException("EventVersion attribute 'patch' cannot be negative, was '%d'".formatted(patch));
        }
    }

    public EventVersion(int major) {
        this(major, 0, 0);
    }

    public EventVersion(int major, int minor) {
        this(major, minor, 0);
    }

    public static EventVersion valueOf(int major) {
        return new EventVersion(major, 0, 0);
    }

    public static EventVersion valueOf(int major, int minor) {
        return new EventVersion(major, minor, 0);
    }

    public static EventVersion valueOf(int major, int minor, int patch) {
        return new EventVersion(major, minor, patch);
    }

    public static EventVersion valueOf(String version) throws IllegalArgumentException {
        if (version == null || version.isBlank()) {
            throw new IllegalArgumentException("Parameter 'version' cannot be null or blank");
        }

        // Regex allows patterns of type: '1', '1.0', '1.0.0', '1_0_0', '1-0-0', '1;0;0'
        // Regex disallows patterns of type: '1_0;0', '1..0', '1.0.0.0', '1.a.0,
        Pattern p = Pattern.compile("^(\\d+)(?:([._\\-;])(\\d+)(?:\\2(\\d+))?)?$");
        Matcher m = p.matcher(version.trim());

        if (!m.matches()) {
            throw new IllegalArgumentException("Parameter 'version' has an invalid version format");
        }

        try {
            int major = Integer.parseInt(m.group(1));
            int minor = m.group(3) != null ? Integer.parseInt(m.group(3)) : 0;
            int patch = m.group(4) != null ? Integer.parseInt(m.group(4)) : 0;
            return new EventVersion(major, minor, patch);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Parameter 'version' has an invalid version format");
        }
    }
}