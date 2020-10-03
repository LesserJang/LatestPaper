package com.jih10157.latestpaper;

final class Version implements Comparable<Version> {

    final int major;
    final int minor;
    final int revision;

    Version(String version) {
        String[] strings = version.split("\\.");
        major = parseIntOrZero(strings[0]);
        minor = parseIntOrZero(strings[1]);
        revision = parseIntOrZero(strings[2]);
    }

    private int parseIntOrZero(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    @Override
    public int compareTo(Version o) {
        if (o == null) {
            return 1;
        }
        return this.compareTo(0, o);
    }

    int get(int i) {
        if (i == 0) {
            return major;
        } else if (i == 1) {
            return minor;
        } else {
            return revision;
        }
    }

    private int compareTo(int i, Version o) {
        int r = Integer.compare(this.get(i), o.get(i));
        if (i != 2 && r == 0) {
            return this.compareTo(i + 1, o);
        } else {
            return r;
        }
    }

    boolean isSmallerThan(Version o) {
        return this.compareTo(o) < 0;
    }

    @Override
    public String toString() {
        return this.major + "." + this.minor + (this.revision > 0 ? ("." + this.revision) : "");
    }
}
