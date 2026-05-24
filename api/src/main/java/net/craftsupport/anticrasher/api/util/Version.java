package net.craftsupport.anticrasher.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Version implements Comparable<Version> {
    private final String original;
    private final List<Integer> parts;

    private Version(String original, List<Integer> parts) {
        this.original = original;
        this.parts = parts;
    }

    public static Version fromString(String version) {
        Objects.requireNonNull(version, "version");
        String normalized = version.strip().replaceFirst("^v", "");
        String[] tokens = normalized.split("[^0-9]+");
        List<Integer> parts = new ArrayList<>();

        for (String token : tokens) {
            if (!token.isEmpty()) {
                parts.add(Integer.parseInt(token));
            }
        }

        return new Version(normalized, parts);
    }

    @Override
    public int compareTo(Version other) {
        int max = Math.max(this.parts.size(), other.parts.size());

        for (int i = 0; i < max; i++) {
            int left = i < this.parts.size() ? this.parts.get(i) : 0;
            int right = i < other.parts.size() ? other.parts.get(i) : 0;

            if (left != right) {
                return Integer.compare(left, right);
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        return original;
    }
}
